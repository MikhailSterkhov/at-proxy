package net.advanceteam.proxy.common.ping.icon;

import com.google.common.io.BaseEncoding;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Favicon {

    public static final TypeAdapter<Favicon> FAVICON_TYPE_ADAPTER = new TypeAdapter<Favicon>() {

        @Override
        public void write(JsonWriter out, Favicon value) throws IOException {
            TypeAdapters.STRING.write(out, value == null ? null : value.getEncoded());
        }

        @Override
        public Favicon read(JsonReader in) throws IOException {
            String enc = TypeAdapters.STRING.read(in);

            return enc == null ? null : create(enc);
        }
    };

    /**
     * The base64 encoded favicon, including MIME header.
     */
    @NonNull
    @Getter
    private final String encoded;

    /**
     * Creates a favicon from an image.
     *
     * @param image the image to create on
     * @return the created favicon instance
     * @throws IllegalArgumentException if the favicon is larger than
     *                                  {@link Short#MAX_VALUE} or not of dimensions 64x64 pixels.
     */
    public static Favicon create(BufferedImage image) {
        if (image.getWidth() != 64 || image.getHeight() != 64) {
            throw new IllegalArgumentException("Server icon must be exactly 64x64 pixels");
        }

        byte[] imageBytes;

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            ImageIO.write(image, "PNG", stream);
            imageBytes = stream.toByteArray();

        } catch (IOException e) {
            throw new AssertionError(e);
        }

        String encoded = "data:image/png;base64," + BaseEncoding.base64().encode(imageBytes);

        if (encoded.length() > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Favicon file too large for server to process");
        }

        return new Favicon(encoded);
    }

    /**
     * Creates a Favicon from an encoded PNG.
     *
     * @param encodedString a base64 mime encoded PNG string
     * @return the created favicon
     * @deprecated Use #create(java.awt.image.BufferedImage) instead
     */
    @Deprecated
    public static Favicon create(String encodedString) {
        return new Favicon(encodedString);
    }
}
