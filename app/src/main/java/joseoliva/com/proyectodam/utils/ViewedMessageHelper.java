package joseoliva.com.proyectodam.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;

import joseoliva.com.proyectodam.providers.AuthProvider;
import joseoliva.com.proyectodam.providers.UserProvider;

public class ViewedMessageHelper {

    public static void updateOnline(boolean estado, final Context context){
        UserProvider userProvider = new UserProvider();
        AuthProvider authProvider = new AuthProvider();
        if(authProvider.getUid() != null){
            if(isApplicationSentToBackground(context)){
                userProvider.updateOnline(authProvider.getUid(),estado);
            }
            else if(estado){
                userProvider.updateOnline(authProvider.getUid(),estado);
            }
        }
    }

    public static boolean isApplicationSentToBackground(final Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
        if(!tasks.isEmpty()){
            ComponentName topActivity = tasks.get(0).topActivity;
            if(!topActivity.getPackageName().equals(context.getPackageName())){
                return true;
            }
        }
        return false;
    }
}
