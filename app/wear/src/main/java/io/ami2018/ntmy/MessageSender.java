package io.ami2018.ntmy;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.support.constraint.Constraints.TAG;

public class MessageSender extends Thread {
    private String path;
    private String message;
    private Context context;

    //constructor
    MessageSender(Context context, String p, String msg) {
        this.context = context;
        path = p;
        message = msg;
    }


    //sends the message via the thread.  this will send to all wearables connected, but
    //since there is (should only?) be one, so no problem.

    // This will be used only to establish a connection to the phone (get the node id)
    public void run() {
        //first get all the nodes.
        Task<List<Node>> nodeListTask =
                Wearable.getNodeClient(context).getConnectedNodes();
        try {
            // Block on a task and get the result synchronously (because this is on a background
            // thread).
            List<Node> nodes = Tasks.await(nodeListTask);

            //Now send the message to each device.
            for (Node node : nodes) {
                Task<Integer> sendMessageTask =
                        Wearable.getMessageClient(context).sendMessage(node.getId(), path, message.getBytes());

                try {
                    // Block on a task and get the result synchronously (because this is on a background
                    // thread).
                    Integer result = Tasks.await(sendMessageTask);
                    Log.v(TAG, "SendThread: message send to " + node.getDisplayName());

                } catch (ExecutionException exception) {
                    Log.e(TAG, "Task failed: " + exception);

                } catch (InterruptedException exception) {
                    Log.e(TAG, "Interrupt occurred: " + exception);
                }

            }

        } catch (ExecutionException exception) {
            Log.e(TAG, "Task failed: " + exception);

        } catch (InterruptedException exception) {
            Log.e(TAG, "Interrupt occurred: " + exception);
        }
    }
}
