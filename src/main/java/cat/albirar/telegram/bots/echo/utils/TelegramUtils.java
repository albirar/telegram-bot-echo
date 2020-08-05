/*
 * This file is part of "bot-echo".
 * 
 * "bot-echo" is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * "bot-echo" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with calendar.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2020 Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 */
package cat.albirar.telegram.bots.echo.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import cat.albirar.telegram.bots.echo.service.ETipusBinari;

/**
 * Utilitats de missatges del telegram.
 *
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
public class TelegramUtils {

    /**
     * Cerca el nom "text" de l'{@code usuari}. 
     * @param usuari L'usuari
     * @param link Si s'ha de crear un link 't.me'
     * @return El text definitiu
     */
	public static String compondreNom(User usuari, boolean link) {
        if(usuari == null) {
            return "desconegut";
        }
        
        if(StringUtils.hasText(usuari.getUserName())) {
            if(link) {
                return String.format("https://t.me/%s", usuari.getUserName());
            } else {
                return String.format("@%s (%d)", usuari.getUserName(), usuari.getId());
            }
        }
        return new StringBuilder()
                .append(usuari.getFirstName())
                .append(StringUtils.hasText(usuari.getLastName()) ? usuari.getLastName() : "")
                .append(" (").append(usuari.getId()).append(")")
                .toString()
                ;
    }
    /**
     * Indica si l'{@code update} conté missatge o no.
     * @param update L'update
     * @return true si és un missatge o una edició de missatge i false en cas contrari
     */
    public static boolean hiHaMissatge(Update update) {
        return (update.hasMessage() || update.hasEditedMessage());
    }
    /**
     * Obté el missatge de l'{@code update}.
     * @param update L'update
     * @return El misssatge o edició del missatge
     */
    public static Message obtenirMissatge(Update update) {
        if(update.hasMessage()) {
            return update.getMessage();
        }
        return update.getEditedMessage();
    }
    /**
     * Obté el moment d'emissió del {@code missatge}.
     * @param missatge El missatge
     * @return El {@link LocalDateTime} d'emissió
     */
    public static LocalDateTime obtenirMomentEmissio(Message missatge) {
        return LocalDateTime.ofEpochSecond(missatge.getDate(), 0, ZoneOffset.UTC);
    }
    /**
     * Obté el text o titol del missatge.
     * @param missatge El missatge
     * @return El text o el títol
     */
    public static Optional<String> obtenirTextOTitol(Message missatge) {
    	// animation, audio, document, photo, video or voice
		if(missatge.hasText() && StringUtils.hasText(missatge.getText())) {
			return Optional.of(missatge.getText());
		}
		if(teCaption(missatge)) {
			// Caption...
			if(StringUtils.hasText(missatge.getCaption())) {
				return Optional.of(missatge.getCaption());
			}
		}
    	return Optional.empty();
    }

    public static boolean teCaption(Message missatge) {
    	return StringUtils.hasText(missatge.getCaption());
    }
    /**
     * Obté el tipus binari associat amb el {@code missatge}, si n'hi ha.
     * @param missatge El missatge
     * @return El {@link ETipusBinari tipus binari} o {@link Optional#empty()} si no n'hi ha pas cap tipus binari o no és conegut
     */
    public static Optional<ETipusBinari> tipusBinariMissatge(Message missatge) {
    	if(missatge.hasAudio()) {
    		return Optional.of(ETipusBinari.AUDIO);
    	}
    	if(missatge.hasDocument()) {
    		return Optional.of(ETipusBinari.DOCUMENT);
    	}
    	if(missatge.hasPhoto()) {
    		return Optional.of(ETipusBinari.FOTO);
    	}
    	if(missatge.hasSticker()) {
    		return Optional.of(ETipusBinari.ENGANXINA);
    	}
    	if(missatge.hasVideo()) {
    		return Optional.of(ETipusBinari.VIDEO);
    	}
    	if(missatge.hasVideoNote()) {
    		return Optional.of(ETipusBinari.VIDEO_NOTA);
    	}
    	if(missatge.hasVoice()) {
    		return Optional.of(ETipusBinari.VEU);
    	}
    	return Optional.empty();
	}
}
