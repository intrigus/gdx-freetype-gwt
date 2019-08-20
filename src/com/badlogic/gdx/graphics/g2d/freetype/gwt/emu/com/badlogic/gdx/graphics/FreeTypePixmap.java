/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.graphics;

import java.nio.ByteBuffer;
import java.nio.DirectReadWriteByteBuffer;
import java.nio.FreeTypeUtil;
import java.nio.HasArrayBufferView;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.typedarrays.shared.ArrayBufferView;
import com.google.gwt.typedarrays.shared.Uint8ClampedArray;

/** @author Simon Gerst */
public class FreeTypePixmap extends Pixmap {

	ByteBuffer buffer;

	public FreeTypePixmap (int width, int height, Format format) {
		super(width, height, format);
	}

	public void setPixelsNull () {
		pixels = null;
	}
	
	/**
	 * Returns a read-only ByteBuffer that represents the **current** status of the
	 * pixels. Any change that is performed to the pixmap **after** this buffer has
	 * been created will **not** be reflected in this buffer!
	 * 
	 * @param pixmap
	 * @return
	 */
	public static ByteBuffer getRealPixels(Pixmap pixmap) {
		if (pixmap.getWidth() == 0 || pixmap.getHeight() == 0) {
			return new DirectReadWriteByteBuffer(0);
		}
		if (pixmap.pixels == null) {
			pixmap.pixels = pixmap.getContext().getImageData(0, 0, pixmap.getWidth(), pixmap.getHeight()).getData();
		}
		ByteBuffer buffer = FreeTypeUtil.newDirectReadWriteByteBuffer(((Uint8ClampedArray) pixmap.pixels).buffer());
		return buffer;
	}

	public ByteBuffer getRealPixels () {
		if (getWidth() == 0 || getHeight() == 0) {
			return new DirectReadWriteByteBuffer(0);
		}
		if (pixels == null) {
			pixels = getContext().getImageData(0, 0, getWidth(), getHeight()).getData();
			buffer = FreeTypeUtil.newDirectReadWriteByteBuffer(((Uint8ClampedArray)pixels).buffer());
			return buffer;
		}
		return buffer;
	}

	public void putPixelsBack (ByteBuffer pixels) {
		if (getWidth() == 0 || getHeight() == 0) return;
		putPixelsBack(((HasArrayBufferView)pixels).getTypedArray(), getWidth(), getHeight(), getContext());

	}

	private native void putPixelsBack (ArrayBufferView pixels, int width, int height, Context2d ctx)/*-{
		var imgData = ctx.createImageData(width, height);
		var data = imgData.data;
		for (var i = 0, len = width * height * 4; i < len; i++) {
			data[i] = pixels[i] & 0xff;
		}
		ctx.putImageData(imgData, 0, 0);
	}-*/;

}
