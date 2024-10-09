package ruzicka.ets.service;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeUtility;
import jakarta.mail.MessagingException;

import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.activation.FileDataSource;
import jakarta.activation.DataHandler;

import java.util.Properties;
import java.util.Date;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.io.IOException;

//---------------------------------------------------------------------------
public class SendMail {
    //---------------------------------------------------------------------------
    String smtpHost;
    String smtpPort;  // 1.0.2
    String sender;
    String encoding;
    String zipfile;

    //------------------------------------------------------------------------
    private String files2zip(String[] files)
    //------------------------------------------------------------------------
            throws FileNotFoundException,IOException
    {
        ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipfile));

        byte[] b = new byte[1024];

        for (int i=0; i<files.length; i++) {

            File     f     = new File(files[i]);
            String   fname = f.getName();
            ZipEntry z     = new ZipEntry(fname);

            zout.putNextEntry(z);

            FileInputStream src = new FileInputStream(f);
            int ii;
            while ((ii = src.read(b)) > 0) {
                zout.write(b,0,ii);
            }
            src.close();
            src = null;
        }

        b = null;
        zout.closeEntry();
        zout.finish();
        zout.close();

        return zipfile;
    }

    //------------------------------------------------------------------------
    public SendMail(String smtpHost, String sender, String zipfile,  String encoding) {
        //------------------------------------------------------------------------
        this.smtpHost=smtpHost;
        this.smtpPort="25";  // 1.0.2
        this.sender=sender;
        this.encoding=encoding;
        this.zipfile=zipfile;

        // 1.0.2: Rozdeleni HOST[:[PORT]] na casti smtpHost a smtpPort
        int n=smtpHost.indexOf(':');
        if (n > -1) {
            if (n < smtpHost.length() -1) // je za dvojteckou neco ?
                smtpPort=smtpHost.substring(n+1);

            this.smtpHost=smtpHost.substring(0,n);
        }
    }

    //------------------------------------------------------------------------
    public SendMail(String smtpHost, String sender, String zipfile) {
        //------------------------------------------------------------------------
        this(smtpHost,sender,zipfile,"iso-8859-2");
    }

    //------------------------------------------------------------------------
    public SendMail(String smtpHost, String sender) {
        //------------------------------------------------------------------------
        this(smtpHost,sender,null,"iso-8859-2");
    }

    //------------------------------------------------------------------------
    public void send(
            //------------------------------------------------------------------------
            String[] recipients,
            String subject,
            String message
    )
            throws MessagingException,UnsupportedEncodingException,
            FileNotFoundException,IOException
    {
        send(recipients,subject,message,null,false);
    }

    //------------------------------------------------------------------------
    public void send(
            //------------------------------------------------------------------------
            String[] recipients,
            String subject,
            String message,
            String[] files
    )
            throws MessagingException,UnsupportedEncodingException,
            FileNotFoundException,IOException
    {
        send(recipients,subject,message,files,false);
    }

    //------------------------------------------------------------------------
    public void sendZipped(
            //------------------------------------------------------------------------
            String[] recipients,
            String subject,
            String message,
            String[] files
    )
            throws MessagingException,UnsupportedEncodingException,
            FileNotFoundException,IOException
    {
        send(recipients,subject,message,files,true);
    }

    //------------------------------------------------------------------------
    public void send(
            //------------------------------------------------------------------------
            String[] recipients,
            String   subject,
            String   message,
            String[] files,
            boolean  pack2zip
    )
            throws MessagingException,UnsupportedEncodingException,
            FileNotFoundException,IOException
    {
        boolean debug = false;

        if (zipfile==null && pack2zip)
            throw new MessagingException("Nelze pozadovat seZIPovani attachmentu; zipfile nebyl v konstruktoru SendMail() zadan");

        //Set the host smtp address
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort); // 1.0.2

        // create some properties and get the default Session
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(debug);

        // create a message
        MimeMessage msg = new MimeMessage(session);

        // set the From and To address
        InternetAddress addressFrom = new InternetAddress(sender);
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            addressTo[i] = new InternetAddress(recipients[i]); //,"Hugo Ventilek");
        }

        msg.setRecipients(Message.RecipientType.TO, addressTo);

        // Optional : You can also set your custom headers in the Email if you want
        msg.addHeader("X-Mailer", "J2SE "+System.getProperty("java.version")+" with JavaMail 1.4.x");

        // Setting the Subject and Content Type
        msg.setSubject(MimeUtility.encodeText(subject,encoding,"B")); // BASE64
        msg.setSentDate(new Date());

        // create the Multipart and its parts to it
        Multipart mp = new MimeMultipart();

        // create and fill message Body part
        MimeBodyPart mbp1 = new MimeBodyPart();
        mbp1.setText(message, encoding);
        mp.addBodyPart(mbp1);

        if (files != null) {
            if (pack2zip) {
                String zipfile=files2zip(files);

                MimeBodyPart mbp2 = new MimeBodyPart();
                FileDataSource fds = new FileDataSource(zipfile);
                mbp2.setDataHandler(new DataHandler(fds));
                mbp2.setFileName(fds.getName());
                mp.addBodyPart(mbp2);
            } else {
                for (int i=0; i < files.length; i++) {
                    // create the second message part
                    MimeBodyPart mbp2 = new MimeBodyPart();

                    // attach the file to the message
                    FileDataSource fds = new FileDataSource(files[i]);
                    mbp2.setDataHandler(new DataHandler(fds));
                    mbp2.setFileName(fds.getName());

                    mp.addBodyPart(mbp2);
                }
            }
        }

        // add the Multipart to the message
        msg.setContent(mp);

//    msg.setContent(message, "text/plain");

        Transport.send(msg);
    }

    //------------------------------------------------------------------------
    public static void main(String[] args) throws Exception {
        //------------------------------------------------------------------------
//    String[] prijemce={"00420724429621@sms.eurotel.cz"};
//    String[] prijemce={"matejka@skp.sk"};
        String[] prijemce={"matejka@koncept.cz"};
        String[] files={"C:\\bin\\build.xml","C:\\bin\\system.txt"};
        try {
//       Posta posta=new Posta("relay.skp.sk","linha@skp.sk");
            SendMail posta=new SendMail("bsdi-proxy","root@koncept.cz","c:\\temp\\mail.zip");
            posta.sendZipped(prijemce,"MOBIL1: žluťoučký kôň","žluťoučký kôň úpěl příšerně ďábelské ódy ?",files);
            posta.send(prijemce,"MOBIL2: žluťoučký kôň","žluťoučký kôň úpěl příšerně ďábelské ódy ?",files);
//       posta.postMail(prijemce,"MOBIL: testik","No nazdar, funguje je to ?");

        } catch (MessagingException mex) {
            mex.printStackTrace();
            Exception ex = null;
            if ((ex = mex.getNextException()) != null) {
                ex.printStackTrace();
            }
        }

//    int i=1;
    }
}
