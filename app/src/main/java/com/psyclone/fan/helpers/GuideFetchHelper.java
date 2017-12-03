package com.psyclone.fan.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class GuideFetchHelper {
    public static JSONArray getShows(String channel, int code, String date) {
        System.out.println("Fan: Refresh started for " + channel + " for date " + date);
        try {
            String url = "http://tv.burrp.com/channel/" + URLEncoder.encode(channel, "UTF-8") + "/" + code + "/" + date + "%200:0:0/1";

            SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mma");

            Document doc = Jsoup.connect(url).get();
            Element result = doc.select("table.result").first();

            JSONArray shows = new JSONArray();

            Elements tableRows = result.select("tr");
            tableRows.remove(0);

            Map<String, JSONObject> detailsMap = new HashMap<>();

            for(Element row: tableRows) {
                JSONObject show = new JSONObject();

                String title = row.select("td.resultTitle > a.title").first().attr("title");
                show.put("showTitle", title);
                show.put("showTime", displayFormat.format(parseFormat.parse(row.select("td.resultTime > b.from").first().text())));
                show.put("showThumb", row.select("td.resultThumb > a > img").first().attr("src"));

                if(detailsMap.containsKey(title))
                    // Do not get details again if the show is airing more than once in a day
                    show.put("showDetails", detailsMap.get(title));
                else {
                    String detailsUrl = row.select("td.resultThumb > a").first().attr("href");
                    Document detailsDoc = Jsoup.connect(detailsUrl).get();
                    Elements topTables = detailsDoc.select("table.meta > tbody > tr");
                    topTables.remove(0);

                    JSONObject details = new JSONObject();

                    for (Element detail : topTables)
                        details.put(detail.select("th").text(), detail.select("td").text());

                    details.put("Show Description", detailsDoc.select("div.synopsis").text().replaceAll("...Read More", "").replaceAll("...Hide", ""));
                    show.put("showDetails", details);

                    detailsMap.put(title, details);
                }

                shows.put(show);
            }

            System.out.println("Fan: Refresh complete for " + channel + " for date " + date);
            return shows;

        } catch(IOException | JSONException | ParseException e) {
            e.printStackTrace();
        }

        System.out.println("Fan: Refresh failed for " + channel + " for date " + date);
        return null;
    }
}
