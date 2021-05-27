package game2048;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

public class DrawUtils {
	
	// centering text
	public static int getMessageWidth(String message, Font font, Graphics2D g) {
		g.setFont(font);
		Rectangle2D bounds = g.getFontMetrics().getStringBounds(message, g);
		return (int) bounds.getWidth();
	}

	public static int getMessageHeight(String message, Font font, Graphics2D g) {
		g.setFont(font);
		if(message.length() == 0) return 0;
		TextLayout tl = new TextLayout(message, font, g.getFontRenderContext());
		return (int) tl.getBounds().getHeight();
	}
	
	public static String formatTime (long millis) {
		String formattedTime;
		
		String hourFormat = "";
		int hours = (int)(millis / 360000);
		if (hours >= 1) {
			millis -= hours * 360000;
			if (hours < 10) {
				hourFormat = "0" + hours;
			} else {
				hourFormat = "" + hours;
			}
			hourFormat += ":";
		}
		
		String minuteFormat;
		int minutes = (int)(millis / 6000);
		if (minutes >= 1) {
			millis -= minutes * 6000;
			if (minutes < 10) {
				minuteFormat = "0" + minutes;
			} else {
				minuteFormat = "" + minutes;
			}
		} else {
			minuteFormat = "00";
		}
		
		String secondFormat;
		int seconds = (int)(millis / 100);
		if (seconds >= 1) {
			millis -= seconds * 100;
			if (seconds < 10) {
				secondFormat = "0" + seconds;
			} else {
				secondFormat = "" + seconds;
			}
		} else {
			secondFormat = "00";
		}
		
		String milliFormat;
		if (millis > 9) {
			milliFormat = "" + millis;
		} else {
			milliFormat = "0" + millis;
		}
		
		formattedTime = hourFormat + minuteFormat + ":" + secondFormat + ":" + milliFormat;
		
		return formattedTime;
	}
}
