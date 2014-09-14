package dk.osaa.psaw.job;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import lombok.extern.java.Log;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.Base64Encoder;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

@Log
public class ImageConverter implements Converter {
	private static final Base64Encoder base64 = new Base64Encoder();
	
	@Override
	public boolean canConvert(Class clazz) {
		return BufferedImage.class.isAssignableFrom(clazz);
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		BufferedImage image = (BufferedImage)value;
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "png", buffer);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Failed to store image to buffer as a png");
			throw new RuntimeException("Failed to store image to buffer as a png",e);
		}
		byte[] bytes = buffer.toByteArray();
		log.info("Stored image to "+bytes.length+" bytes");
        writer.setValue(base64.encode(bytes));
	}
	
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		ByteArrayInputStream is = new ByteArrayInputStream(base64.decode(reader.getValue()));
		try {
			return ImageIO.read(is);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to load image from buffer as a png");
			throw new RuntimeException("Failed to load image from buffer as a png",e);
		}
	}

}
