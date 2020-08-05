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
 * Copyright (C) 2020 Octavi Fornés
 */
package cat.albirar.telegram.bots.echo;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.telegram.telegrambots.meta.api.objects.Message;

import cat.albirar.telegram.bots.echo.service.models.MissatgeBean;

/**
 * Servei 'ECHO'.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Validated
public interface IServeiEcho {
    /**
     * Notifica de l'arribada d'un text.
     * @param operadorTelegram L'operador telegram
     * @param moment el moment del missatge
     * @param emissor L'emissor del missatge
     * @param titol El títol
     * @param contingut El contingut del missatge
     */
    public void notificarMissatge(@NotNull ITelegramOperations operadorTelegram, @NotNull LocalDateTime moment, @NotBlank String emissor, @NotBlank String titol, @NotBlank String contingut);
    /**
     * Notifica de l'arribada d'un missatge.
     * @param operadorTelegram L'operador telegram
     * @param moment el moment del missatge
     * @param emissor L'emissor del missatge
     * @param titol El títol
     * @param message
     */
    public void notificarMissatge(@NotNull ITelegramOperations operadorTelegram, @NotNull LocalDateTime moment, @NotBlank String emissor, @NotBlank String titol, @NotNull Message message);
    /**
     * Notifica de l'arribada d'un missatge.
     * @param operadorTelegram L'operador telegram
     * @param missatge El missatge
     */
    public void notificarMissatge(@NotNull ITelegramOperations operadorTelegram, @NotNull MissatgeBean missatge);
}
