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
import org.crsh.text.ui.Element;
import org.crsh.text.ui.UIBuilder;
import org.crsh.text.ui.UIBuilderRenderable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.util.Collections;

public class RenderPrintWriter extends PrintWriter {

  /** . */
  private final RenderWriter out;

  public RenderPrintWriter(ScreenContext out) {
    super(new RenderWriter(out));

    //
    this.out = (RenderWriter)super.out;
  }

  public RenderPrintWriter(ScreenContext out, Closeable closeable) {
    super(new RenderWriter(out, closeable));

    //
    this.out = (RenderWriter)super.out;
  }

  public final boolean isEmpty() {
    return out.isEmpty();
  }

  public final void print(Object obj, Color foreground) {
    try {
      out.provide(Style.style(foreground));
    }
    catch (InterruptedIOException x) {
      Thread.currentThread().interrupt();
    }
    catch (IOException x) {
      setError();
    }
    print(obj);
    try {
      out.provide(Style.reset);
    }
    catch (InterruptedIOException x) {
      Thread.currentThread().interrupt();
    }
    catch (IOException x) {
      setError();
    }
  }

  public final void println(Object obj, Color foreground) {
    print(obj, Style.style(foreground));
    println();
  }

  public final void print(Object obj, Color foreground, Color background) {
    try {
      out.provide(Style.style(foreground, background));
    }
    catch (InterruptedIOException x) {
      Thread.currentThread().interrupt();
    }
    catch (IOException x) {
      setError();
    }
    print(obj);
    try {
      out.provide(Style.reset);
    }
    catch (InterruptedIOException x) {
      Thread.currentThread().interrupt();
    }
    catch (IOException x) {
      setError();
    }
  }

  public final void println(Object obj, Color foreground, Color background) {
    print(obj, Style.style(foreground, background));
    println();
  }

  public final void print(Object obj, Decoration decoration) {
    try {
      out.provide(Style.style(decoration));
    }
    catch (InterruptedIOException x) {
      Thread.currentThread().interrupt();
    }
    catch (IOException x) {
      setError();
    }
    print(obj);
    try {
      out.provide(Style.reset);
    }
    catch (InterruptedIOException x) {
      Thread.currentThread().interrupt();
    }
    catch (IOException x) {
      setError();
    }
  }

  public final void println(Object obj, Decoration decoration) {
    print(obj, Style.style(decoration));
    println();
  }

  public final void print(Object obj, Decoration decoration, Color foreground) {
    print(obj, Style.style(decoration, foreground));
    println();
  }

  public final void println(Object obj, Decoration decoration, Color foreground) {
    print(obj, Style.style(decoration, foreground, null));
    println();
  }

  public final void print(Object obj, Decoration decoration, Color foreground, Color background) {
    print(obj, Style.style(decoration, foreground, background));
    println();
  }

  public final void println(Object obj, Decoration decoration, Color foreground, Color background) {
    print(obj, Style.style(decoration, foreground, background));
    println();
  }

  public final void print(Object obj, Style style) {
    try {
      out.provide(style);
    }
    catch (InterruptedIOException x) {
      Thread.currentThread().interrupt();
    }
    catch (IOException x) {
      setError();
    }
    print(obj);
    try {
      out.provide(Style.reset);
    }
    catch (InterruptedIOException x) {
      Thread.currentThread().interrupt();
    }
    catch (IOException x) {
      setError();
    }
  }

  public final void println(Object obj, Style style) {
    print(obj, style);
    println();
  }

  /**
   * Groovy left shift operator.
   *
   * @param o the appended
   * @return this object
   */
  public final RenderPrintWriter leftShift(Object o) {
    if (o instanceof Style) {
      try {
        out.provide((Style)o);
      }
      catch (InterruptedIOException x) {
        Thread.currentThread().interrupt();
      }
      catch (IOException x) {
        setError();
      }
    } else if (o instanceof Decoration) {
      try {
        out.provide((Style.style((Decoration)o)));
      }
      catch (InterruptedIOException x) {
        Thread.currentThread().interrupt();
      }
      catch (IOException x) {
        setError();
      }
    } else if (o instanceof Color) {
      try {
        out.provide(Style.style((Color)o));
      }
      catch (InterruptedIOException x) {
        Thread.currentThread().interrupt();
      }
      catch (IOException x) {
        setError();
      }
    } else {
      print(o);
    }
    return this;
  }

  public final RenderPrintWriter cls() {
    try {
      out.provide(CLS.INSTANCE);
    }
    catch (InterruptedIOException x) {
      Thread.currentThread().interrupt();
    }
    catch (IOException x) {
      setError();
    }
    return this;
  }

  @Override
  public void println(Object x) {
    print(x);
    println();
  }

  public void show(Element element) {
    element.render(new RenderAppendable(this.out));
  }

  @Override
  public void print(Object obj) {
    if (obj instanceof UIBuilder) {
      RenderAppendable out = new RenderAppendable(this.out);
      new UIBuilderRenderable().renderer(Collections.singleton((UIBuilder)obj).iterator()).render(out);
    } else if (obj instanceof Element) {
      RenderAppendable out = new RenderAppendable(this.out);
      ((Element)obj).renderer().render(out);
    } else {
      super.print(obj);
    }
    
    //EXPORT FILE IN ls COMMAND: add code
    this.out.exportLSResult2File();
  }
  
  //EXPORT FILE IN ls COMMAND:getOut()  
  public RenderWriter getOut() {
	return out;
  }
}
