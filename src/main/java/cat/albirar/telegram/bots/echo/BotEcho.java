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

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import cat.albirar.telegram.bots.echo.service.models.MissatgeBean;
import cat.albirar.telegram.bots.echo.utils.TelegramUtils;

/**
 * Bot per a fer echo.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Component
@Profile("default")
public class BotEcho extends TelegramLongPollingBot implements ITelegramOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotEcho.class);
    
    @Value("${bot.token}")
    private String token;
    @Value("${bot.username}")
    private String username;
    @Value("${bot.missatges.benvinguda}")
    private String benvinguda;
    @Value("${bot.missatges.resposta}")
    private String respostaMissatge;
    @Autowired
    public IServeiEcho serveiEcho;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdateReceived(Update update) {
        if(TelegramUtils.hiHaMissatge(update)) {
            Message message;
            User user;
            
            GetFile f = new GetFile();
            f.setFileId("");
            
            
            LOGGER.debug("Tractament del update: {}", update);
            message = TelegramUtils.obtenirMissatge(update);
            if(message.hasText() && message.getText().startsWith("/start")) {
            	// Benvinguda i au
            	enviarRespostaText(update,  benvinguda);
            } else {
	            user = message.getFrom();
	            serveiEcho.notificarMissatge(this, MissatgeBean.builder()
	            		.emissor(TelegramUtils.compondreNom(user, true))
	            		.moment(TelegramUtils.obtenirMomentEmissio(message))
	            		.message(message)
	            		.build()
	            		);
	            
	            // Ok, respondre que moltes gràcies!
	            enviarRespostaText(update, respostaMissatge);
            }
        } else {
            LOGGER.warn("Update desconegut i no tractat: {}", update);
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getBotUsername() {
        return username;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBotToken() {
        return token;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File descarregarArxiu(String fileId) throws TelegramApiException {
    	GetFile gFile;
    	org.telegram.telegrambots.meta.api.objects.File fTelegram;
    	File f;
    	
    	LOGGER.debug("Obtenir dades de l'arxiu amb id {}", fileId);
    	// Obtenir les dades...
    	gFile = new GetFile();
    	gFile.setFileId(fileId);
    	fTelegram = execute(gFile);
    	LOGGER.debug("Dades de l'arxiu: {}", fTelegram);
    	LOGGER.debug("Descarrego arxiu amb id {} i path {}...", fileId, fTelegram.getFilePath());
    	f = downloadFile(fTelegram.getFilePath());
    	LOGGER.debug("Arxiu amb id {} descarregat al path {}", fileId, f.getAbsolutePath());
    	return f;
    	
    }
    /**
     * {@inheritDoc}
     */
    public void enviarRespostaText(Update update, String text) {
        SendMessage sendMessage;

        sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(text);
        sendMessage.enableMarkdown(true);
        try {
            execute(sendMessage);
            LOGGER.debug("Update {} tractat!", update);
        }
        catch(TelegramApiException e) {
            LOGGER.error(String.format("No s'ha pogut respondre al update %s (%s)", update, e.getMessage()), e);
        }
	}
}
