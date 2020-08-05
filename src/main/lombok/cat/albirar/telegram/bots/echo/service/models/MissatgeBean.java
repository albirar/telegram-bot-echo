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
package cat.albirar.telegram.bots.echo.service.models;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.telegram.telegrambots.meta.api.objects.Message;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Bean d'un missatge.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Validated
public class MissatgeBean {
    @Setter(onParam_ = {@NotNull})
    private LocalDateTime moment;
    @Setter(onParam_ = {@NotBlank})
    private String emissor;
    @Setter(onParam_ = {@NotNull})
    private Message message;

    public static LocalDateTime obtenirMomentEmissio(Message message) {
        return LocalDateTime.ofEpochSecond(message.getDate(), 0, ZoneOffset.UTC);
    }
}
