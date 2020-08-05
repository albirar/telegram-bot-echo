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
package cat.albirar.telegram.bots.echo.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.activation.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import cat.albirar.telegram.bots.echo.IServeiEcho;
import cat.albirar.telegram.bots.echo.ITelegramOperations;
import cat.albirar.telegram.bots.echo.email.BotEchoDataSource;
import cat.albirar.telegram.bots.echo.service.models.DescriptorArxiuTelegram;
import cat.albirar.telegram.bots.echo.service.models.MissatgeBean;
import cat.albirar.telegram.bots.echo.utils.TelegramUtils;

/**
 * Implementació del servei {@link IServeiEcho}.
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
@Service("serveiEcho")
@Profile("default")
public class ServeiEcho implements IServeiEcho {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServeiEcho.class);

	/** Nom d'arxius de veu. */
	private static final String NOM_VEU = "VEU";
	/** Nom d'arxius de vídeo. */
	private static final String NOM_VIDEO = "VIDEO";
	/** Plantilla per a noms d'enganxines. */
	private static final String TPL_ENGANXINA = "%s.webp";
	/**
	 * Plantilla per a nomenar fotos.
	 * Arguments:
	 * <ol>
	 * <li>Índex (normalment comença en 1 i va pujant a cada foto)
	 * </ol>
	 */
	private static final String TPL_NOM_FOTO = "FOTO-%d.jpg";
	/**
	 * Mimetype de les enganxines
	 */
	private static final String MIMETYPE_ENGANXINA = "image/webp";
	/** Nom de la plantilla per a l'email. */
    private static final String PLANTILLA = "mailTemplate";

    @Value("${bot.email.to}")
    private String emailTo;

    @Value("${bot.email.from}")
    private String emailFrom;

    @Autowired
    private JavaMailSender emailSender;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void notificarMissatge(ITelegramOperations operador, LocalDateTime moment, String emissor, String titol, String contingut) {
        LOGGER.debug("Notificar missatge!");
        enviarEmail(moment, emissor, titol, contingut, new ArrayList<>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notificarMissatge(ITelegramOperations operador, LocalDateTime moment, String emissor, String titol, Message message) {
        notificarMissatge(operador, MissatgeBean.builder()
                .moment(moment)
                .emissor(emissor)
                .message(message).build());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void notificarMissatge(ITelegramOperations operadorTelegram, MissatgeBean missatge) {
        LOGGER.debug("Notificar missatge!");
    	List<DataSource> adjunts;
    	
    	adjunts = obtenirAdjunts(operadorTelegram,missatge.getMessage());
    	enviarEmail(missatge.getMoment()
    			, missatge.getEmissor()
    			, "Missatge de bústia"
    			, TelegramUtils.obtenirTextOTitol(missatge.getMessage()).orElse("SENSE TEXT")
    			, adjunts);
    }
    /**
     * Refactorització per a enviar un email.
     * @param moment El moment, requerit
     * @param emissor L'emissor, requerit
     * @param titol El títol
     * @param contingut El cos
     * @param adjunts Adjunts, que pot estar buit
     */
    private void enviarEmail(LocalDateTime moment, String emissor, String titol, String contingut, List<DataSource> adjunts) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, !CollectionUtils.isEmpty(adjunts));
            messageHelper.setFrom(emailFrom);
            messageHelper.setTo(emailTo);
            messageHelper.setSubject(titol);
            messageHelper.setText(composicio(moment, emissor, contingut), true);
            if(!CollectionUtils.isEmpty(adjunts)) {
            	for(DataSource d : adjunts) {
            		messageHelper.addAttachment(d.getName(), d);
            	}
            }
        };
        
        try {
            emailSender.send(messagePreparator);
            LOGGER.debug("Missatge notificat!");
        } catch (MailException e) {
            LOGGER.error(String.format("En enviar el missatge rebut el %s de l'emissor %s, amb el contingut %s (%s)", moment, emissor, contingut, e.getMessage()), e);
        }
    }
    /**
     * Crea l'adjunt a enviar que s'ha d'extreure del {@code missatge}.
     * @param missatge El missatge
     * @return L'adjunt
     */
    private List<DataSource> obtenirAdjunts(ITelegramOperations operadorTelegram, Message missatge) {
    	List<DataSource> retorn;
    	File arxiu;
    	List<DescriptorArxiuTelegram> idArxius;
    	Optional<ETipusBinari> tipus;
    	int n;
    	String contentType;
    	
    	tipus = TelegramUtils.tipusBinariMissatge(missatge);
    	idArxius = new ArrayList<>();
    	retorn = new ArrayList<>();
    	
    	if(tipus.isPresent()) {
    		switch(tipus.get()) {
    		case ANIMACIO:
    			idArxius.add(DescriptorArxiuTelegram.builder()
    					.fileId(missatge.getAnimation().getFileId())
    					.nom(missatge.getAnimation().getFileName())
    					.contenType(missatge.getAnimation().getMimetype())
    					.build());
    			break;
    			
    		case AUDIO:
    			idArxius.add(DescriptorArxiuTelegram.builder()
    					.fileId(missatge.getAudio().getFileId())
    					.nom(missatge.getAudio().getTitle())
    					.contenType(missatge.getAudio().getMimeType())
    					.build());
    			break;
    			
    		case DOCUMENT:
    			idArxius.add(DescriptorArxiuTelegram.builder()
    					.fileId(missatge.getDocument().getFileId())
    					.nom(missatge.getDocument().getFileName())
    					.contenType(missatge.getDocument().getMimeType())
    					.build());
    			break;
    			
    		case ENGANXINA:
    			idArxius.add(DescriptorArxiuTelegram.builder()
    					.fileId(missatge.getSticker().getFileId())
    					.nom(String.format(TPL_ENGANXINA, missatge.getSticker().getSetName()))
    					.contenType(MIMETYPE_ENGANXINA)
    					.build());
    			break;
    			
    		case FOTO:
    			n = 1;
    			for(PhotoSize p : missatge.getPhoto()) {
        			idArxius.add(DescriptorArxiuTelegram.builder()
        					.fileId(p.getFileId())
        					.nom(p.hasFilePath() ? p.getFilePath() : String.format(TPL_NOM_FOTO, n))
        					.build());
        			n++;
    			}
    			break;
    			
    		case VIDEO:
    			idArxius.add(DescriptorArxiuTelegram.builder()
    					.fileId(missatge.getVideo().getFileId())
    					.nom(NOM_VIDEO)
    					.contenType(missatge.getVideo().getMimeType())
    					.build()
    					);
    			break;
    			
    		case VIDEO_NOTA:
    			idArxius.add(DescriptorArxiuTelegram.builder()
    					.fileId(missatge.getVideoNote().getFileId())
    					.nom("VIDEO-NOTA.mp4")
    					.build()
    					);
    			break;
    			
    		case VEU:
    			idArxius.add(DescriptorArxiuTelegram.builder()
    					.fileId(missatge.getVoice().getFileId())
    					.contenType(missatge.getVoice().getMimeType())
    					.nom(NOM_VEU)
    					.build()
    					);
    		}
    		try {
	    		for(DescriptorArxiuTelegram desc: idArxius) {
	    			arxiu = operadorTelegram.descarregarArxiu(desc.getFileId());
	    			if(desc.getContenType().equals(DescriptorArxiuTelegram.CONTENT_TYPE_BY_FILE)) {
	    				contentType = tipusMimePerPath(arxiu.getAbsolutePath());
	    			} else {
	    				contentType = desc.getContenType();
	    			}
	    			retorn.add(new BotEchoDataSource(arxiu, desc.getNom(), contentType));
	    		}
    		} catch (TelegramApiException | IOException e) {
    			LOGGER.error(String.format("En obtenir els objectes binaris del missatge '%s' (%s)", missatge, e.getMessage()), e);
    			retorn.clear();
    		}
    	}
		return retorn;
	}
    /**
     * Extreu el tipus mime pel nom de l'arxiu.
     * @param path El path
     * @return El tipus mime o 
     * @throws IOException Si hi ha excepcions en obtenir el tipus mime
     */
    private String tipusMimePerPath(String path) throws IOException {
    	return Files.probeContentType(Paths.get(path));
    }
    /**
     * Munta el missatge d'email amb la plantilla que toca.
     * @param moment El moment d'emissió
     * @param emissor L'emissor
     * @param contingut El contingut
     * @return El text del missatge.
     */
    private String composicio(LocalDateTime moment, String emissor, String contingut) {
        Context context = new Context();
        context.setVariable("moment", moment);
        context.setVariable("emissor", emissor);
        context.setVariable("linkEmissor", Boolean.valueOf(emissor.startsWith("http://") || emissor.startsWith("https://")));
        context.setVariable("contingut", contingut);
        return templateEngine.process(PLANTILLA, context);
    }
}
