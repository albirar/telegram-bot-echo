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
package cat.albirar.telegram.bots.echo;

import java.io.File;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Definició d'operacions disponibles per a Telegram.
 * 
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
public interface ITelegramOperations {
	/**
	 * Descarrega un arxiu segons el nom.
	 * @param fileId L'identificador de l'arxiu
	 * @return l'arxiu
     * @throws TelegramApiException Si hi ha cap problema en operar amb l'api de telegram
	 */
	public File descarregarArxiu(String fileId) throws TelegramApiException;
    /**
     * Envia un misssatge de resposta al xat de l'usuari {@link update.getMessage().getCharId() Message#getChatId()}.
     * @param update Update original
     * @param textResposta El text de la resposta
     */
    public void enviarRespostaText(Update update, String textResposta);
}
