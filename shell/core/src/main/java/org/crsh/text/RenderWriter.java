/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.crsh.text;

import org.crsh.shell.ScreenContext;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class RenderWriter extends Writer implements ScreenContext<Chunk> {

  /** . */
  private final ScreenContext out;

  /** . */
  private final Closeable closeable;

  /** . */
  private boolean closed;

  /** . */
  private boolean empty;
  
//BEGIN UPDATE: EXPORT FILE IN ls COMMAND
 private ChunkBuffer buffer;
 
 private String sLS_ExportPath = "/tmp/tempfile.txt";
 
 public void setsLS_ExportPath(String sLS_ExportPath) {
		this.sLS_ExportPath = sLS_ExportPath;
 }
 
 public void exportLSResult2File() {
	  try {
		  //File in = new File("/home/tunglt/writecrashgroovy.txt");
		  File in = new File(sLS_ExportPath);
		  if(in.exists()) {
			  in.delete();			  
		  }
		  in.createNewFile();
	      FileOutputStream fos = new FileOutputStream(sLS_ExportPath, true);
	  	  PrintWriter ps = new PrintWriter(fos);
	  	  ps.write(buffer.toString());
	  	  ps.close();
	  }
	  catch (Exception e) {
	  	  e.printStackTrace();
	  }
  }
  // END UPDATE: EXPORT FILE IN ls COMMAND 

  public RenderWriter(ScreenContext out) throws NullPointerException {
    this(out, null);
  }

  public RenderWriter(ScreenContext out, Closeable closeable) throws NullPointerException {
    if (out == null) {
      throw new NullPointerException("No null appendable expected");
    }

    //
    this.out = out;
    this.empty = true;
    this.closeable = closeable;
    
    // EXPORT FILE IN ls COMMAND: buffer
    buffer = new ChunkBuffer();
  }

  public boolean isEmpty() {
    return empty;
  }

  public int getWidth() {
    return out.getWidth();
  }

  public int getHeight() {
    return out.getHeight();
  }

  public Class<Chunk> getConsumedType() {
    return Chunk.class;
  }

  public void provide(Chunk element) throws IOException {
    if (element instanceof Text) {
      Text text = (Text)element;
      empty &= text.getText().length() == 0;
    }
    out.provide(element);
    
    // EXPORT FILE IN ls COMMAND
    buffer.provide(element);
  }

  @Override
  public void write(char[] cbuf, int off, int len) throws IOException {
    if (closed) {
      throw new IOException("Already closed");
    }
    if (len > 0) {
      Text text = new Text();
      text.buffer.append(cbuf, off, len);
      provide(text);
    }
  }

  @Override
  public void flush() throws IOException {
    if (closed) {
      throw new IOException("Already closed");
    }
    out.flush();
  }

  @Override
  public void close() throws IOException {
    if (!closed) {
      closed = true;
      if (closeable != null) {
        closeable.close();
      }
    }
  }
}
