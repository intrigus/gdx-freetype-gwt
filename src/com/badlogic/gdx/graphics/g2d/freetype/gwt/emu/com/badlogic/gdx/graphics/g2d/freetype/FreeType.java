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

package com.badlogic.gdx.graphics.g2d.freetype;

import java.nio.ByteBuffer;
import java.nio.FreeTypeUtil;
import java.nio.HasArrayBufferView;
import java.nio.IntBuffer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FreeTypePixmap;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.LongMap;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.typedarrays.shared.ArrayBufferView;
import com.google.gwt.typedarrays.shared.Int8Array;

/** @author Simon Gerst */
public class FreeType {

	private static native void nativeFree (int address)/*-{
		$wnd.Module._free(address);
	}-*/;

	private static class Pointer {
		public int address;

		Pointer (int address) {
			this.address = address;
		}
	}

	public static class Library extends Pointer implements Disposable {
		LongMap<Integer> fontData = new LongMap<Integer>();

		Library (int address) {
			super(address);
		}

		@Override
		public void dispose () {
			doneFreeType(address);
			for (Integer address : fontData.values()) {
				nativeFree(address);
			}
		}

		private static native void doneFreeType (int library)/*-{
			$wnd.Module._c_Library_doneFreeType(library);
		}-*/;

		public Face newFace (FileHandle font, int faceIndex) {
			byte[] data = font.readBytes();
			return newMemoryFace(data, data.length, faceIndex);
		}

		public Face newMemoryFace (byte[] data, int dataSize, int faceIndex) {
			ByteBuffer buffer = BufferUtils.newByteBuffer(data.length);
			BufferUtils.copy(data, 0, buffer, data.length);
			return newMemoryFace(buffer, faceIndex);
		}

		public Face newMemoryFace (ByteBuffer buffer, int faceIndex) {
			ArrayBufferView buf = ((HasArrayBufferView)buffer).getTypedArray();
			int[] addressToFree = new int[] {0}; // Hacky way to get two return values
			int face = newMemoryFace(address, buf, buffer.remaining(), faceIndex, addressToFree);
			if (face == 0) {
				if (addressToFree[0] != 0) { // 'Zero' would mean allocating the buffer failed
					nativeFree(addressToFree[0]);
				}
				throw new GdxRuntimeException("Couldn't load font");
			} else {
				fontData.put(face, addressToFree[0]);
				return new Face(face, this);
			}
		}

		private static native int newMemoryFace (int library, ArrayBufferView data, int dataSize, int faceIndex,
			int[] outAddressToFree)/*-{
			var address = $wnd.Module._malloc(data.length);
			outAddressToFree[0] = address;
			$wnd.Module.writeArrayToMemory(data, address);
			return $wnd.Module._c_Library_newMemoryFace(library, address,
					dataSize, faceIndex);
		}-*/;

		public Stroker createStroker () {
			int stroker = strokerNew(address);
			if (stroker == 0) throw new GdxRuntimeException("Couldn't create FreeType stroker");
			return new Stroker(stroker);
		}

		private static native int strokerNew (int library)/*-{
			return $wnd.Module._c_Library_strokerNew(library);
		}-*/;
	}

	public static class Face extends Pointer implements Disposable {
		Library library;

		public Face (int address, Library library) {
			super(address);
			this.library = library;
		}

		@Override
		public void dispose () {
			doneFace(address);
			Integer freeAddress = library.fontData.get(address);
			if (freeAddress != 0) { // Don't free 'zero' address
				library.fontData.remove(address);
				nativeFree(freeAddress);
			}
		}

		private static native void doneFace (int face)/*-{
			$wnd.Module._c_Face_doneFace(face);
		}-*/;

		public int getFaceFlags () {
			int flags = getFaceFlags(address);
			return flags;
		}

		private static native int getFaceFlags (int face)/*-{
			return $wnd.Module._c_Face_getFaceFlags(face);
		}-*/;

		public int getStyleFlags () {
			int flags = getStyleFlags(address);
			return flags;
		}

		private static native int getStyleFlags (int face)/*-{
			return $wnd.Module._c_Face_getStyleFlags(face);
		}-*/;

		public int getNumGlyphs () {
			int flags = getNumGlyphs(address);
			return flags;
		}

		private static native int getNumGlyphs (int face)/*-{
			return $wnd.Module._c_Face_getNumGlyphs(face);
		}-*/;

		public int getAscender () {
			int flags = getAscender(address);
			return flags;
		}

		private static native int getAscender (int face)/*-{
			return $wnd.Module._c_Face_getAscender(face);
		}-*/;

		public int getDescender () {
			int flags = getDescender(address);
			return flags;
		}

		private static native int getDescender (int face)/*-{
			return $wnd.Module._c_Face_getDescender(face);
		}-*/;

		public int getHeight () {
			int flags = getHeight(address);
			return flags;
		}

		private static native int getHeight (int face)/*-{
			return $wnd.Module._c_Face_getHeight(face);
		}-*/;

		public int getMaxAdvanceWidth () {
			int flags = getMaxAdvanceWidth(address);
			return flags;
		}

		private static native int getMaxAdvanceWidth (int face)/*-{
			return $wnd.Module._c_Face_getMaxAdvanceWidth(face);
		}-*/;

		public int getMaxAdvanceHeight () {
			int flags = getMaxAdvanceHeight(address);
			return flags;
		}

		private static native int getMaxAdvanceHeight (int face)/*-{
			return $wnd.Module._c_Face_getMaxAdvanceHeight(face);
		}-*/;

		public int getUnderlinePosition () {
			int flags = getUnderlinePosition(address);
			return flags;
		}

		private static native int getUnderlinePosition (int face)/*-{
			return $wnd.Module._c_Face_getUnderlinePosition(face);
		}-*/;

		public int getUnderlineThickness () {
			int flags = getUnderlineThickness(address);
			return flags;
		}

		private static native int getUnderlineThickness (int face)/*-{
			return $wnd.Module._c_Face_getUnderlineThickness(face);
		}-*/;

		public boolean selectSize (int strikeIndex) {
			boolean flags = selectSize(address, strikeIndex);
			return flags;
		}

		private static native boolean selectSize (int face, int strike_index)/*-{
			return !!$wnd.Module._c_Face_selectSize(face, strike_index);
		}-*/;

		public boolean setCharSize (int charWidth, int charHeight, int horzResolution, int vertResolution) {
			boolean flags = setCharSize(address, charWidth, charHeight, horzResolution, vertResolution);
			return flags;
		}

		private static native boolean setCharSize (int face, int charWidth, int charHeight, int horzResolution,
			int vertResolution)/*-{
			return !!$wnd.Module._c_Face_setCharSize(face, charWidth,
					charHeight, horzResolution, vertResolution);
		}-*/;

		public boolean setPixelSizes (int pixelWidth, int pixelHeight) {
			boolean flags = setPixelSizes(address, pixelWidth, pixelHeight);
			return flags;
		}

		private static native boolean setPixelSizes (int face, int pixelWidth, int pixelHeight)/*-{
			return !!$wnd.Module._c_Face_setPixelSizes(face, pixelWidth,
					pixelHeight);
		}-*/;

		public boolean loadGlyph (int glyphIndex, int loadFlags) {
			boolean flags = loadGlyph(address, glyphIndex, loadFlags);
			return flags;
		}

		public static native boolean loadGlyph (int face, int glyphIndex, int loadFlags)/*-{
			return !!$wnd.Module._c_Face_loadGlyph(face, glyphIndex, loadFlags);
		}-*/;

		public boolean loadChar (int charCode, int loadFlags) {
			boolean flags = loadChar(address, charCode, loadFlags);
			return flags;
		}

		private static native boolean loadChar (int face, int charCode, int loadFlags)/*-{
			return !!$wnd.Module._c_Face_loadChar(face, charCode, loadFlags);
		}-*/;

		public GlyphSlot getGlyph () {
			return new GlyphSlot(getGlyph(address));
		}

		private static native int getGlyph (int face)/*-{
			return $wnd.Module._c_Face_getGlyph(face);
		}-*/;

		public Size getSize () {
			return new Size(getSize(address));
		}

		private static native int getSize (int face)/*-{
			return $wnd.Module._c_Face_getSize(face);
		}-*/;

		public boolean hasKerning () {
			boolean flags = hasKerning(address);
			return flags;
		}

		private static native boolean hasKerning (int face)/*-{
			return !!$wnd.Module._c_Face_hasKerning(face);
		}-*/;

		public int getKerning (int leftGlyph, int rightGlyph, int kernMode) {
			int flags = getKerning(address, leftGlyph, rightGlyph, kernMode);
			return flags;
		}

		private static native int getKerning (int face, int leftGlyph, int rightGlyph, int kernMode)/*-{
			return $wnd.Module._c_Face_getKerning(face, leftGlyph, rightGlyph,
					kernMode);
		}-*/;

		public int getCharIndex (int charCode) {
			int flags = getCharIndex(address, charCode);
			return flags;
		}

		public static native int getCharIndex (int face, int charCode)/*-{
			return $wnd.Module._c_Face_getCharIndex(face, charCode);
		}-*/;

	}

	public static class Size extends Pointer {
		Size (int address) {
			super(address);
		}

		public SizeMetrics getMetrics () {
			return new SizeMetrics(getMetrics(address));
		}

		private static native int getMetrics (int address)/*-{
			return $wnd.Module._c_Size_getMetrics(address);
		}-*/;

	}

	public static class SizeMetrics extends Pointer {
		SizeMetrics (int address) {
			super(address);
		}

		public int getXppem () {
			int flags = getXppem(address);
			return flags;
		}

		private static native int getXppem (int metrics)/*-{
			return $wnd.Module._c_SizeMetrics_getXppem(metrics);
		}-*/;

		public int getYppem () {
			int flags = getYppem(address);
			return flags;
		}

		private static native int getYppem (int metrics)/*-{
			return $wnd.Module._c_SizeMetrics_getYppem(metrics);
		}-*/;

		public int getXScale () {
			int flags = getXscale(address);
			return flags;
		}

		private static native int getXscale (int metrics)/*-{
			return $wnd.Module._c_SizeMetrics_getXscale(metrics);
		}-*/;

		public int getYscale () {
			int flags = getYscale(address);
			return flags;
		}

		private static native int getYscale (int metrics)/*-{
			return $wnd.Module._c_SizeMetrics_getYscale(metrics);
		}-*/;

		public int getAscender () {
			int flags = getAscender(address);
			return flags;
		}

		private static native int getAscender (int metrics)/*-{
			return $wnd.Module._c_SizeMetrics_getAscender(metrics);
		}-*/;

		public int getDescender () {
			int flags = getDescender(address);
			return flags;
		}

		private static native int getDescender (int metrics)/*-{
			return $wnd.Module._c_SizeMetrics_getDescender(metrics);
		}-*/;

		public int getHeight () {
			int flags = getHeight(address);
			return flags;
		}

		private static native int getHeight (int metrics)/*-{
			return $wnd.Module._c_SizeMetrics_getHeight(metrics);
		}-*/;

		public int getMaxAdvance () {
			int flags = getMaxAdvance(address);
			return flags;
		}

		private static native int getMaxAdvance (int metrics)/*-{
			return $wnd.Module._c_SizeMetrics_getMaxAdvance(metrics);
		}-*/;

	}

	public static class GlyphSlot extends Pointer {
		GlyphSlot (int address) {
			super(address);
		}

		public GlyphMetrics getMetrics () {
			return new GlyphMetrics(getMetrics(address));
		}

		private static native int getMetrics (int slot)/*-{
			return $wnd.Module._c_GlyphSlot_getMetrics(slot);
		}-*/;

		public int getLinearHoriAdvance () {
			int flags = getLinearHoriAdvance(address);
			return flags;
		}

		private static native int getLinearHoriAdvance (int slot)/*-{
			return $wnd.Module._c_GlyphSlot_getLinearHoriAdvance(slot);
		}-*/;

		public int getLinearVertAdvance () {
			int flags = getLinearVertAdvance(address);
			return flags;
		}

		private static native int getLinearVertAdvance (int slot)/*-{
			return $wnd.Module._c_GlyphSlot_getLinearVertAdvance(slot);
		}-*/;

		public int getAdvanceX () {
			int flags = getAdvanceX(address);
			return flags;
		}

		private static native int getAdvanceX (int slot)/*-{
			return $wnd.Module._c_GlyphSlot_getAdvanceX(slot);
		}-*/;

		public int getAdvanceY () {
			int flags = getAdvanceY(address);
			return flags;
		}

		private static native int getAdvanceY (int slot)/*-{
			return $wnd.Module._c_GlyphSlot_getAdvanceY(slot);
		}-*/;

		public int getFormat () {
			int flags = getFormat(address);
			return flags;
		}

		private static native int getFormat (int slot)/*-{
			return $wnd.Module._c_GlyphSlot_getFormat(slot);
		}-*/;

		public Bitmap getBitmap () {
			return new Bitmap(getBitmap(address));
		}

		private static native int getBitmap (int slot)/*-{
			return $wnd.Module._c_GlyphSlot_getBitmap(slot);
		}-*/;

		public int getBitmapLeft () {
			int flags = getBitmapLeft(address);
			return flags;
		}

		private static native int getBitmapLeft (int slot)/*-{
			return $wnd.Module._c_GlyphSlot_getBitmapLeft(slot);
		}-*/;

		public int getBitmapTop () {
			int flags = getBitmapTop(address);
			return flags;
		}

		private static native int getBitmapTop (int slot)/*-{
			return $wnd.Module._c_GlyphSlot_getBitmapTop(slot);
		}-*/;

		public boolean renderGlyph (int renderMode) {
			boolean flags = renderGlyph(address, renderMode);
			return flags;
		}

		public static native boolean renderGlyph (int slot, int renderMode)/*-{
			return !!$wnd.Module._c_GlyphSlot_renderGlyph(slot, renderMode);
		}-*/;

		public Glyph getGlyph () {
			int glyph = getGlyph(address);
			if (glyph == 0) throw new GdxRuntimeException("Couldn't get glyph");
			return new Glyph(glyph);
		}

		private static native int getGlyph (int glyphSlot)/*-{
			return $wnd.Module._c_GlyphSlot_getGlyph(glyphSlot);
		}-*/;

	}

	public static class Glyph extends Pointer implements Disposable {
		private boolean rendered;

		Glyph (int address) {
			super(address);
		}

		@Override
		public void dispose () {
			done(address);
		}

		private static native void done (int glyph)/*-{
			$wnd.Module._c_Glyph_done(glyph);
		}-*/;

		private int bTI (boolean bool) {
			return bool == true ? 1 : 0;
		}

		public void strokeBorder (Stroker stroker, boolean inside) {
			address = strokeBorder(address, stroker.address, bTI(inside));
		}

		private static native int strokeBorder (int glyph, int stroker, int inside)/*-{
			return $wnd.Module._c_Glyph_strokeBorder(glyph, stroker, inside);
		}-*/;

		public void toBitmap (int renderMode) {
			int bitmap = toBitmap(address, renderMode);
			if (bitmap == 0) throw new GdxRuntimeException("Couldn't render glyph");
			address = bitmap;
			rendered = true;
		}

		private static native int toBitmap (int glyph, int renderMode)/*-{
			return $wnd.Module._c_Glyph_toBitmap(glyph, renderMode);
		}-*/;

		public Bitmap getBitmap () {
			if (!rendered) {
				throw new GdxRuntimeException("Glyph is not yet rendered");
			}
			return new Bitmap(getBitmap(address));
		}

		private static native int getBitmap (int glyph)/*-{
			return $wnd.Module._c_Glyph_getBitmap(glyph);
		}-*/;

		public int getLeft () {
			if (!rendered) {
				throw new GdxRuntimeException("Glyph is not yet rendered");
			}
			return getLeft(address);
		}

		private static native int getLeft (int glyph)/*-{
			return $wnd.Module._c_Glyph_getLeft(glyph);
		}-*/;

		public int getTop () {
			if (!rendered) {
				throw new GdxRuntimeException("Glyph is not yet rendered");
			}
			return getTop(address);
		}

		private static native int getTop (int glyph)/*-{
			return $wnd.Module._c_Glyph_getTop(glyph);
		}-*/;

	}

	public static class Bitmap extends Pointer {
		Bitmap (int address) {
			super(address);
		}

		public int getRows () {
			return getRows(address);
		}

		private static native int getRows (int bitmap)/*-{
			return $wnd.Module._c_Bitmap_getRows(bitmap);
		}-*/;

		public int getWidth () {
			return getWidth(address);
		}

		private static native int getWidth (int bitmap)/*-{
			return $wnd.Module._c_Bitmap_getWidth(bitmap);
		}-*/;

		public int getPitch () {
			return getPitch(address);
		}

		private static native int getPitch (int bitmap)/*-{
			return $wnd.Module._c_Bitmap_getPitch(bitmap);
		}-*/;

		public ByteBuffer getBuffer () {
			if (getRows() == 0)
				// Issue #768 - CheckJNI frowns upon env->NewDirectByteBuffer with NULL buffer or capacity 0
				// "JNI WARNING: invalid values for address (0x0) or capacity (0)"
				// FreeType sets FT_Bitmap::buffer to NULL when the bitmap is empty (e.g. for ' ')
				// JNICheck is on by default on emulators and might have a point anyway...
				// So let's avoid this and just return a dummy non-null non-zero buffer
				return BufferUtils.newByteBuffer(1);
			int offset = getBufferAddress(address);
			int length = getBufferSize(address);
			Int8Array as = getBuffer(address, offset, length);
			ArrayBuffer aBuf = as.buffer();
			ByteBuffer buf = FreeTypeUtil.newDirectReadWriteByteBuffer(aBuf, length, offset);

			return buf;
		}

		public Pixmap getPixmap (Format format, Color color, float gamma) {
			int width = getWidth(), rows = getRows();
			ByteBuffer src = getBuffer();
			FreeTypePixmap pixmap;
			ByteBuffer changedPixels;
			int pixelMode = getPixelMode();
			int rowBytes = Math.abs(getPitch()); // We currently ignore negative pitch.
			if (color == Color.WHITE && pixelMode == FT_PIXEL_MODE_GRAY && rowBytes == width && gamma == 1) {
				pixmap = new FreeTypePixmap(width, rows, Format.Alpha);
				changedPixels = pixmap.getRealPixels();
				BufferUtils.copy(src, changedPixels, pixmap.getRealPixels().capacity());
			} else {
				pixmap = new FreeTypePixmap(width, rows, Format.RGBA8888);
				int rgba = Color.rgba8888(color);
				byte[] srcRow = new byte[rowBytes];
				int[] dstRow = new int[width];
				changedPixels = pixmap.getRealPixels();
				IntBuffer dst = changedPixels.asIntBuffer();
				if (pixelMode == FT_PIXEL_MODE_MONO) {
					// Use the specified color for each set bit.
					for (int y = 0; y < rows; y++) {
						src.get(srcRow);
						for (int i = 0, x = 0; x < width; i++, x += 8) {
							byte b = srcRow[i];
							for (int ii = 0, n = Math.min(8, width - x); ii < n; ii++) {
								if ((b & (1 << (7 - ii))) != 0)
									dstRow[x + ii] = rgba;
								else
									dstRow[x + ii] = 0;
							}
						}
						dst.put(dstRow);
					}
				} else {
					// Use the specified color for RGB, blend the FreeType bitmap with alpha.
					int rgb = rgba & 0xffffff00;
					int a = rgba & 0xff;
					for (int y = 0; y < rows; y++) {
						src.get(srcRow);
						for (int x = 0; x < width; x++) {
							float alpha;

							// Zero raised to any power is always zero.
							// 255 (=one) raised to any power is always one
							// This means that we only have to calculate Math.pow() when alpha is NOT zero and NOT one
							if ((srcRow[x] & 0xff) == 0) {
								alpha = 0f;
							} else if ((srcRow[x] & 0xff) == 255) {
								alpha = 1f;
							} else {
								alpha = (float)Math.pow(((srcRow[x] & 0xff) / 255f), gamma); // Inverse gamma.
							}
							dstRow[x] = rgb | (int)(a * alpha);
						}
						dst.put(dstRow);
					}
				}
			}

			pixmap.putPixelsBack(changedPixels);
			pixmap.setPixelsNull();
			Pixmap p = pixmap;

			Pixmap converted = pixmap;
			if (pixmap.getFormat() != null && format != pixmap.getFormat()) {
				converted = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), format);
				pixmap.setBlending(Blending.None);
				converted.drawPixmap(pixmap, 0, 0);
				pixmap.dispose();
			}
			return converted;
		}

		private static native int getBufferAddress (int bitmap)/*-{
			return $wnd.Module._c_Bitmap_getBufferAddress(bitmap);
		}-*/;

		private static native int getBufferSize (int bitmap)/*-{
			return $wnd.Module._c_Bitmap_getBufferSize(bitmap);
		}-*/;

		private static native Int8Array getBuffer (int bitmap, int offset, int length)/*-{
			var buff = $wnd.Module.HEAP8.subarray(offset, offset + length);
			return buff;
		}-*/;

		public int getNumGray () {
			return getNumGray(address);
		}

		private static native int getNumGray (int bitmap)/*-{
			return $wnd.Module._c_Bitmap_getNumGray(bitmap);
		}-*/;

		public int getPixelMode () {
			return getPixelMode(address);
		}

		private static native int getPixelMode (int bitmap)/*-{
			return $wnd.Module._c_Bitmap_getPixelMode(bitmap);
		}-*/;

	}

	public static class GlyphMetrics extends Pointer {
		GlyphMetrics (int address) {
			super(address);
		}

		public int getWidth () {
			return getWidth(address);
		}

		private static native int getWidth (int metrics)/*-{
			return $wnd.Module._c_GlyphMetrics_getWidth(metrics);
		}-*/;

		public int getHeight () {
			return getHeight(address);
		}

		private static native int getHeight (int metrics)/*-{
			return $wnd.Module._c_GlyphMetrics_getHeight(metrics);
		}-*/;

		public int getHoriBearingX () {
			return getHoriBearingX(address);
		}

		private static native int getHoriBearingX (int metrics)/*-{
			return $wnd.Module._c_GlyphMetrics_getHoriBearingX(metrics);
		}-*/;

		public int getHoriBearingY () {
			return getHoriBearingY(address);
		}

		private static native int getHoriBearingY (int metrics)/*-{
			return $wnd.Module._c_GlyphMetrics_getHoriBearingY(metrics);
		}-*/;

		public int getHoriAdvance () {
			return getHoriAdvance(address);
		}

		private static native int getHoriAdvance (int metrics)/*-{
			return $wnd.Module._c_GlyphMetrics_getHoriAdvance(metrics);
		}-*/;

		public int getVertBearingX () {
			return getVertBearingX(address);
		}

		private static native int getVertBearingX (int metrics)/*-{
			return $wnd.Module._c_GlyphMetrics_getVertBearingX(metrics);
		}-*/;

		public int getVertBearingY () {
			return getVertBearingY(address);
		}

		private static native int getVertBearingY (int metrics)/*-{
			return $wnd.Module._c_GlyphMetrics_getVertBearingY(metrics);
		}-*/;

		public int getVertAdvance () {
			return getVertAdvance(address);
		}

		private static native int getVertAdvance (int metrics)/*-{
			return $wnd.Module._c_GlyphMetrics_getVertAdvance(metrics);
		}-*/;

	}

	public static class Stroker extends Pointer implements Disposable {
		Stroker (int address) {
			super(address);
		}

		public void set (int radius, int lineCap, int lineJoin, int miterLimit) {
			set(address, radius, lineCap, lineJoin, miterLimit);
		}

		private static native void set (int stroker, int radius, int lineCap, int lineJoin, int miterLimit)/*-{
			$wnd.Module._c_Stroker_set(stroker, radius, lineCap, lineJoin,
					miterLimit);
		}-*/;

		@Override
		public void dispose () {
			done(address);

		}

		private static native void done (int stroker)/*-{
			$wnd.Module._c_Stroker_done(stroker);
		}-*/;

	}

	public static int FT_PIXEL_MODE_NONE = 0;
	public static int FT_PIXEL_MODE_MONO = 1;
	public static int FT_PIXEL_MODE_GRAY = 2;
	public static int FT_PIXEL_MODE_GRAY2 = 3;
	public static int FT_PIXEL_MODE_GRAY4 = 4;
	public static int FT_PIXEL_MODE_LCD = 5;
	public static int FT_PIXEL_MODE_LCD_V = 6;

	private static int encode (char a, char b, char c, char d) {
		return (a << 24) | (b << 16) | (c << 8) | d;
	}

	public static int FT_ENCODING_NONE = 0;
	public static int FT_ENCODING_MS_SYMBOL = encode('s', 'y', 'm', 'b');
	public static int FT_ENCODING_UNICODE = encode('u', 'n', 'i', 'c');
	public static int FT_ENCODING_SJIS = encode('s', 'j', 'i', 's');
	public static int FT_ENCODING_GB2312 = encode('g', 'b', ' ', ' ');
	public static int FT_ENCODING_BIG5 = encode('b', 'i', 'g', '5');
	public static int FT_ENCODING_WANSUNG = encode('w', 'a', 'n', 's');
	public static int FT_ENCODING_JOHAB = encode('j', 'o', 'h', 'a');
	public static int FT_ENCODING_ADOBE_STANDARD = encode('A', 'D', 'O', 'B');
	public static int FT_ENCODING_ADOBE_EXPERT = encode('A', 'D', 'B', 'E');
	public static int FT_ENCODING_ADOBE_CUSTOM = encode('A', 'D', 'B', 'C');
	public static int FT_ENCODING_ADOBE_LATIN_1 = encode('l', 'a', 't', '1');
	public static int FT_ENCODING_OLD_LATIN_2 = encode('l', 'a', 't', '2');
	public static int FT_ENCODING_APPLE_ROMAN = encode('a', 'r', 'm', 'n');

	public static int FT_FACE_FLAG_SCALABLE = (1 << 0);
	public static int FT_FACE_FLAG_FIXED_SIZES = (1 << 1);
	public static int FT_FACE_FLAG_FIXED_WIDTH = (1 << 2);
	public static int FT_FACE_FLAG_SFNT = (1 << 3);
	public static int FT_FACE_FLAG_HORIZONTAL = (1 << 4);
	public static int FT_FACE_FLAG_VERTICAL = (1 << 5);
	public static int FT_FACE_FLAG_KERNING = (1 << 6);
	public static int FT_FACE_FLAG_FAST_GLYPHS = (1 << 7);
	public static int FT_FACE_FLAG_MULTIPLE_MASTERS = (1 << 8);
	public static int FT_FACE_FLAG_GLYPH_NAMES = (1 << 9);
	public static int FT_FACE_FLAG_EXTERNAL_STREAM = (1 << 10);
	public static int FT_FACE_FLAG_HINTER = (1 << 11);
	public static int FT_FACE_FLAG_CID_KEYED = (1 << 12);
	public static int FT_FACE_FLAG_TRICKY = (1 << 13);

	public static int FT_STYLE_FLAG_ITALIC = (1 << 0);
	public static int FT_STYLE_FLAG_BOLD = (1 << 1);

	public static int FT_LOAD_DEFAULT = 0x0;
	public static int FT_LOAD_NO_SCALE = 0x1;
	public static int FT_LOAD_NO_HINTING = 0x2;
	public static int FT_LOAD_RENDER = 0x4;
	public static int FT_LOAD_NO_BITMAP = 0x8;
	public static int FT_LOAD_VERTICAL_LAYOUT = 0x10;
	public static int FT_LOAD_FORCE_AUTOHINT = 0x20;
	public static int FT_LOAD_CROP_BITMAP = 0x40;
	public static int FT_LOAD_PEDANTIC = 0x80;
	public static int FT_LOAD_IGNORE_GLOBAL_ADVANCE_WIDTH = 0x200;
	public static int FT_LOAD_NO_RECURSE = 0x400;
	public static int FT_LOAD_IGNORE_TRANSFORM = 0x800;
	public static int FT_LOAD_MONOCHROME = 0x1000;
	public static int FT_LOAD_LINEAR_DESIGN = 0x2000;
	public static int FT_LOAD_NO_AUTOHINT = 0x8000;

	public static int FT_LOAD_TARGET_NORMAL = 0x0;
	public static int FT_LOAD_TARGET_LIGHT = 0x10000;
	public static int FT_LOAD_TARGET_MONO = 0x20000;
	public static int FT_LOAD_TARGET_LCD = 0x30000;
	public static int FT_LOAD_TARGET_LCD_V = 0x40000;

	public static int FT_RENDER_MODE_NORMAL = 0;
	public static int FT_RENDER_MODE_LIGHT = 1;
	public static int FT_RENDER_MODE_MONO = 2;
	public static int FT_RENDER_MODE_LCD = 3;
	public static int FT_RENDER_MODE_LCD_V = 4;
	public static int FT_RENDER_MODE_MAX = 5;

	public static int FT_KERNING_DEFAULT = 0;
	public static int FT_KERNING_UNFITTED = 1;
	public static int FT_KERNING_UNSCALED = 2;

	public static int FT_STROKER_LINECAP_BUTT = 0;
	public static int FT_STROKER_LINECAP_ROUND = 1;
	public static int FT_STROKER_LINECAP_SQUARE = 2;

	public static int FT_STROKER_LINEJOIN_ROUND = 0;
	public static int FT_STROKER_LINEJOIN_BEVEL = 1;
	public static int FT_STROKER_LINEJOIN_MITER_VARIABLE = 2;
	public static int FT_STROKER_LINEJOIN_MITER = FT_STROKER_LINEJOIN_MITER_VARIABLE;
	public static int FT_STROKER_LINEJOIN_MITER_FIXED = 3;

	public static Library initFreeType () {
		int address = initFreeTypeJni();
		if (address == 0)
			throw new GdxRuntimeException("Couldn't initialize FreeType library");
		else
			return new Library(address);
	}

	private static native int initFreeTypeJni ()/*-{
		return $wnd.Module._c_FreeType_initFreeTypeJni();
	}-*/;

	public static int toInt (int value) {
		return ((value + 63) & -64) >> 6;
	}

}
