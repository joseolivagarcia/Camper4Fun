package joseoliva.com.proyectodam.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Map;
import java.util.Random;

import joseoliva.com.proyectodam.R;
import joseoliva.com.proyectodam.channel.NotificationHelper;
import joseoliva.com.proyectodam.models.Message;
import joseoliva.com.proyectodam.receivers.MessageReceiver;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {

    public static final String NOTIFICATION_REPLY = "NotificationReply";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String,String> data = remoteMessage.getData(); //aqui obtenemos la info que llega de la notificacion
        String titulo = data.get("titulo");
        String cuerpo = data.get("cuerpo");

        if(titulo != null){
            if(titulo.equals("NUEVO MENSAJE")){

                showNotificationMessage(data);
            }
            else{
                showNotification(titulo,cuerpo);
            }

        }
    }

    private void showNotification(String titulo,String cuerpo){
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotification(titulo,cuerpo);
        Random random = new Random();
        int n = random.nextInt(10000);
        notificationHelper.getManager().notify(n,builder.build());
    }

    private void showNotificationMessage(Map<String, String> data){
        String imageSender = data.get("imageSender");
        String imageReceiver = data.get("imageReceiver");
        getImageSender(data,imageSender,imageReceiver);
    }

    private void getImageSender(final Map<String, String> data,final String imageSender,final String imageReceiver) {

        new Handler(Looper.getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.with(getApplicationContext())
                                .load(imageSender)
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmapSender, Picasso.LoadedFrom from) {
                                        getImageReceiver(data,imageReceiver,bitmapSender);
                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable errorDrawable) {
                                        getImageReceiver(data,imageReceiver,null);
                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });

                    }
                });
    }

    private void getImageReceiver(final Map<String, String> data,String imageReceiver,Bitmap bitmapSender){
        Picasso.with(getApplicationContext())
                .load(imageReceiver)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmapReceiver, Picasso.LoadedFrom from) {
                        notifyMessage(data, bitmapSender,bitmapReceiver);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        notifyMessage(data,bitmapSender,null);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    private void notifyMessage(Map<String,String> data, Bitmap bitmapSender, Bitmap bitmapReceiver){

        String usernameSender = data.get("usernameSender");
        String usernameReceiver = data.get("usernameReceiver");
        String lastMessage = data.get("lastMessage");
        String messagesJson = data.get("messages");
        String imageSender = data.get("imageSender");
        String imageReceiver = data.get("imageReceiver");

        String idSender = data.get("idSender");
        String idReceiver = data.get("idReceiver");
        String idChat = data.get("idChat");
        final int idNotification = Integer.parseInt(data.get("idNotification"));

        Intent intent = new Intent(this, MessageReceiver.class);
        intent.putExtra("idSender",idSender);
        intent.putExtra("idReceiver",idReceiver);
        intent.putExtra("idChat",idChat);
        intent.putExtra("idNotification",idNotification);
        intent.putExtra("usernameSender",usernameSender);
        intent.putExtra("usernameReceiver",usernameReceiver);
        intent.putExtra("imageSender",imageSender);
        intent.putExtra("imageReceiver",imageReceiver);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteInput remoteInput = new RemoteInput.Builder(NOTIFICATION_REPLY).setLabel("Tu mensaje...").build();

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher_foreground,
                "Responder",
                pendingIntent)
                .addRemoteInput(remoteInput)
                .build();


        Gson gson = new Gson();
        Message[] messages = gson.fromJson(messagesJson,Message[].class);

        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder =
                notificationHelper.getNotificationMessage(
                        messages,
                        usernameSender,
                        usernameReceiver,
                        lastMessage,
                        bitmapSender,
                        bitmapReceiver,
                        action
                );
        notificationHelper.getManager().notify(idNotification,builder.build());
    }
}
