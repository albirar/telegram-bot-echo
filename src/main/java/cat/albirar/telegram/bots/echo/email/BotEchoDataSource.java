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
package cat.albirar.telegram.bots.echo.email;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

/**
 * {@link DataSource} per a adjunts.
 *
 * @author Octavi Forn&eacute;s &lt;<a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>&gt;
 * @since 1.0.0
 */
public class BotEchoDataSource implements DataSource {
	private File arxiu;
	private String nom;
	private String contentType;
	
	/**
	 * Constructor únic.
	 * @param arxiu L'arxiu
	 * @param nom El nom de l'attachment
	 * @param contentType El tipus mime
	 */
	public BotEchoDataSource(File arxiu, String nom, String contentType) {
		super();
		this.arxiu = arxiu;
		this.nom = nom;
		this.contentType = contentType;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(arxiu);
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new UnsupportedOperationException("Read-only javax.activation.DataSource");
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public String getName() {
		return nom;
	}

}
