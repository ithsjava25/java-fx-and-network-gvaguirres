package com.example;

import javafx.application.Platform;

public class RunOnFx {
    /**
     * Execute the given Runnable on the JavaFX Application Thread when available.
     *
     * If already on the JavaFX Application Thread the task runs immediately; otherwise it is
     * scheduled to run on the JavaFX thread. If the JavaFX platform is not initialized, the
     * task is executed directly as a fallback.
     *
     * @param task the Runnable to execute
     */
    public static void runOnFx(Runnable task){

        try{
            if (Platform.isFxApplicationThread()) {
                task.run();
            } else{
                Platform.runLater(task);
            }
        }catch (IllegalStateException notInitialized){
            task.run();
        }
    }
}