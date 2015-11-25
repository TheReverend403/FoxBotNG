/*
 * This file is part of FoxBotNG.
 *
 * FoxBotNG is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBotNG is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package co.foxdev.foxbotng.utils;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class UrlExtractor {
    private static final Pattern URL_PATTERN = Pattern.compile(
            ".*((https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]).*");

    public static boolean hasUrl(String message) {
        Matcher matcher = URL_PATTERN.matcher(message);
        return matcher.matches();
    }

    public static String getFrom(String message) {
        Matcher matcher = URL_PATTERN.matcher(message);
        if (matcher.matches()) {
            String url = matcher.group(1);
            log.debug("Found URL '{}' in '{}'", url, message);
            return url;
        }
        return null;
    }

    public static String parseUrlForTitle(String url) {
        try {
            Document doc = Jsoup.connect(url).followRedirects(true).maxBodySize(655360).timeout(5000).get();
            String title = doc.title();
            if (title.isEmpty()) {
                title = "No title";
            }
            return title.substring(0, title.length() >= 100 ? 100 : title.length());
        } catch (IOException ex) {
            log.warn("Error parsing title from {}: {}", url, ex.getLocalizedMessage());
            return ex.getLocalizedMessage();
        }
    }
}
