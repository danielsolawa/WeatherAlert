package com.danielsolawa.locationapp.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.danielsolawa.locationapp.model.WeatherData;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by NeverForgive on 2018-06-03.
 */

public class EmailUtils {


    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    public static boolean isEmailValid(String email){
        return email.matches(EMAIL_PATTERN);
    }

    private static String getProperty(String key, Context ctx) throws IOException {
        Properties properties = new Properties();
        AssetManager am = ctx.getAssets();
        InputStream inputStream = am.open("email.properties");
        properties.load(inputStream);
        return properties.getProperty(key);
    }

    public static void sendEmail(Context ctx, WeatherData weatherData, String recipients){
        final String username = fetchProperty("username", ctx);
        final String password = fetchProperty("password", ctx);
        final String host = fetchProperty("host", ctx);
        final String auth = fetchProperty("auth", ctx);
        final String socketFactory = fetchProperty("socketFactory", ctx);
        final int port = Integer.parseInt(fetchProperty("port", ctx));


        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.socketFactory.class", socketFactory);
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.port", port);
        Session session = Session.getDefaultInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        MimeMessage mm = new MimeMessage(session);
        final String message = generateMessage(weatherData);

        try {
            mm.setFrom(new InternetAddress(username));
            mm.setRecipients(Message.RecipientType.CC,
                    InternetAddress.parse(recipients));
            mm.setSubject("Weather Alert! " + weatherData.getLocality().getName());
            mm.setContent(message, "text/html; charset=utf-8");


            Transport.send(mm);
        } catch (MessagingException e) {
            e.printStackTrace();
        }


    }

    private static String generateMessage(WeatherData weatherData) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!doctype html>\n" + "<html>\n" + "<head>\n");
        sb.append("<style type=\"text/css\">");
        sb.append(".content{\n" +
                "\tbackground: #1e5799; /* Old browsers */\n" +
                "\tbackground: -moz-linear-gradient(-45deg, #1e5799 0%, #2989d8 50%, #207cca 51%, #7db9e8 100%); /* FF3.6-15 */\n" +
                "\tbackground: -webkit-linear-gradient(-45deg, #1e5799 0%,#2989d8 50%,#207cca 51%,#7db9e8 100%); /* Chrome10-25,Safari5.1-6 */\n" +
                "\tbackground: linear-gradient(135deg, #1e5799 0%,#2989d8 50%,#207cca 51%,#7db9e8 100%); /* W3C, IE10+, FF16+, Chrome26+, Opera12+, Safari7+ */\n" +
                "\tfilter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#1e5799', endColorstr='#7db9e8',GradientType=1 ); /* IE6-9 fallback on horizontal gradient */\n" +
                "\tborder-style: solid;\n" +
                "\tborder-width: 1px;\n" +
                "\tborder-radius: 5px;\n" +
                "\tborder-color: #FFFFFF;\n" +
                "\tpadding: 10px;\n" +
                "}");
        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<div class='content'>");
        sb.append("<h3>"
                + weatherData.getDescription() +"</h3>");
        sb.append("<h3>"+weatherData.getTemp()+ "\u00b0C" +"</h3>");
        sb.append("<h3>"+weatherData.getLocality().getName()
                + " lat:" + weatherData.getLocality().getLatitude()
                + " long:" + weatherData.getLocality().getLongitude()+"</h3>");
        sb.append("<h3>"+weatherData.getDate() +"</h3>");
        sb.append("</div>");
        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();
    }

    private static String fetchProperty(String key, Context ctx){

        try {
            return getProperty(key, ctx);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
