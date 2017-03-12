package com.example.campusmap;

import com.example.campusmap.data.server.RootJson;
import com.example.campusmap.fragment.MenuPlannerFragment;
import com.example.campusmap.util.Json;
import com.example.campusmap.util.Request;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

public class RequestTest {

    @Test
    public void server() {
        RootJson rootJson = Request.get("http://34.194.159.2:8000/api/versions")
                .send(RootJson.class);

        System.out.println(Json.from(rootJson));
    }

    @Test
    public void gntechMenu() throws IOException {
        Document document = Jsoup.connect(MenuPlannerFragment.GNTechURL).get();
        Elements tags = document.select("table");
        Element tbody = tags.get(1).select("tbody").first();
        Element tr = tbody.children().get(1); // 중식
        Element td = tr.children().get(2); //
        String html = td.html().replaceAll("(\n| )", "")/*.replace("&amp;","&").replace("\n", "").replace(" ", "")*/;
        String[] meals = html.split("<br><br>");

        // <>...<>...<>...
        for (String meal : meals) {
            String[] menus = meal.split("<br>");
            for (String menu : menus) {
                if (menu.startsWith("<")) {
                    System.out.println("메뉴 : " + menu);
                } else {
                    int index = menu.indexOf("</");
                    if (index >= 0) {
                        menu = menu.substring(0, index);
                    }
                    System.out.println("    " + menu);
                }
            }
//            System.out.println(meal);
        }

//        String[] menus = html.split("<br>");
//        for (String menu : menus) {
//            int index = menu.indexOf("</");
//            if (index >= 0) {
//                menu = menu.substring(0, index);
//            }
//            System.out.println(menu);
//        }
    }
}
