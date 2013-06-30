package util;

import apcontroller.Main;
import data.JAPInfo;
import database.JDataManagement;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.log4j.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import log.JLogger;

/**
 *
 * @author controlador
 */
public class JMail {
    /*
     * To change this template, choose Tools | Templates
     * and open the template in the editor.
     */
    
    private static boolean sendMail(String strSubject, String strMessage, String strFrom, String strTo, String strServer) throws AddressException, RuntimeException, MessagingException {
        
        Session session = Session.getDefaultInstance(getProperties(strServer), null);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(strFrom)); //Seta o remetente
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(strTo)); //Define o destinatário
            message.setSubject(strSubject); //Define o assunto
            message.setContent(strMessage, "text/html"); //Mensagem do email
            
            Transport.send(message); //Envia o email

            return true;
            
        } catch (AddressException e) {
            throw new AddressException ("Error when addressing email: " + e);
        } catch (RuntimeException e) {
            throw new RuntimeException ("Error when starting server mail: " + e);
        } catch (MessagingException e) {
            throw new MessagingException ("Email not sent: " + e);
        }
    }

    //Método que retorna as propriedades de configuração do servidor de email
    private static Properties getProperties(String server) {
        Properties props = new Properties();
        props.put("mail.smtp.host", server); //SMTP do seu servidor de email
        //props.put("mail.smtp.socketFactory.port", "465"); //Porta do servidor smtp
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //Define a conexão do tipo SSL
        props.put("mail.smtp.auth", "false"); //Define que é necessário autenticação
        //props.put("mail.smtp.port", "465"); //Porta do seu servidor smtp

        return props;
    }
    
    public static void sendUnreachableMail(JAPInfo apInfo) {
        
        //APLICAR O MUTEX, PARA TODOS OS PROCESSOS CONCORRENTES NO AP.
        //COMO, GETCHANNEL, GETPOWER, SETCHANNEL, SET POWER, UPDATEREGIONAP, UPDATEFILEENABLED
        if(apInfo.getEmailSent() == 0) {
            // Email ainda não foi enviado no dia
            String from = JDataManagement.getPropertyValue("SenderMail");
            String to = JDataManagement.getPropertyValue("ReceiverMail");
            String server = JDataManagement.getPropertyValue("ServerMail");
            
            String subject = "[SCIFI] ATENÇÃO: O Ponto de Acesso de IP " + apInfo.getIP() + " está INCOMUNICANTE !";
            String content = "<font face=\"Sans serif\" size=\"2\">" +
                                "<b>Atenção !</b> <br><br>" +
                                "O seguinte Ponto de Acesso está incomunicante: <br><br>" +
                                    "<b>IP: " + apInfo.getIP() + "<br>" +
                                    "MAC: " + apInfo.getMAC() + "</b><br>" +
                                    "Localização: " + apInfo.getLocalization() + "<br>" +
                                    "Região: " + JDataManagement.getRegionById(apInfo.getRegion()).getDescription() + "<br>" +
                                "<br>" +
                                "<hr width=\"98%\">" +
                                "<br>" +
                                "Esta mensagem é automática, favor não respondê-la." +
                                "</font>";
            
            boolean sendMsg = false;
            
            try {
                sendMsg = sendMail(subject, content, from, to, server);
                
                Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Email sent for " + apInfo.getIP() + " ! From: " + from + " - To: " + to);
            } catch (AddressException e) {
                Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + e);
            } catch (RuntimeException e) {
                Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + e);
            } catch (MessagingException e) {
                Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + e);
            } finally {
                if (sendMsg) {
                    JDataManagement.updateEmailSent(apInfo.getMAC());
                    
                } else {
                    Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + " Mail not sent for " + apInfo.getIP() + " !");
                }
            }

        }
    }
    
    public static void sendDailyReportMail() {
        
        // Email ainda não foi enviado no dia
        String from = JDataManagement.getPropertyValue("SenderMail");
        String to = JDataManagement.getPropertyValue("ReceiverMail");
        String server = JDataManagement.getPropertyValue("ServerMail");
        ArrayList<JAPInfo> listAP = JDataManagement.loadAllAPListUnreachables();

        String subject = "[SCIFI] Relatório diário de APs incomunicantes (Região: " + JDataManagement.getRegionById(Main.getRegionId()).getDescription() + ") !";
        String content = "<font face=\"Sans serif\" size=\"2\">";                            
        
                            if(listAP.size() >= 1) {
                                content = content + "<b>Atenção !</b> <br><br>" + 
                                "Os seguintes Pontos de Acessos habilitados estão incomunicantes, no momento: <br><br>";
                            
                                for(int nInd = 0; nInd < listAP.size(); nInd++)
                                {                                        
                                    content = content + "<b>IP - " + listAP.get(nInd).getIP() + "<br>" + 
                                                        "MAC - " + listAP.get(nInd).getIP() + "</b><br>" + 
                                                        "Localização - " + listAP.get(nInd).getLocalization() + "<br>" + 
                                                        "Região - " + JDataManagement.getRegionById(listAP.get(nInd).getRegion()).getDescription() + "<br><br>";
                                }
                            } else {
                                content = content + "<p>Sua rede está estável. No momento, não há nenhum ponto de acesso habilitado e incomunicante.</p>";
                            }                                                      
                            
                            content = content + "<br>" +
                            "<hr width=\"98%\">" +
                            "<br>" +
                            "Esta mensagem é automática, favor não respondê-la." +
                            "</font>";

        try {
            sendMail(subject, content, from, to, server);
            
            if(listAP.size() >= 1) {
                Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Daily report email sent for " + listAP.size() + " APs ! ");
            } else {
                Logger.getLogger(Main.JAVA_LOG).info(JLogger.getDateTime() + " " + JLogger.getTime() + " Daily report email sent with no AP ! ");
            }
            
        } catch (AddressException e) {
            Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + e);
        } catch (RuntimeException e) {
            Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + e);
        } catch (MessagingException e) {
            Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + e);
        } finally {
            if(!JDataManagement.updateRestartEmailSent()) {
                Logger.getLogger(Main.JAVA_LOG).error(JLogger.getDateTime() + " " + JLogger.getTime() + "Error when updating all EmailSent to zero.");
            }
        }
    }
}
