package kz.hxncus.mc.railcarcoupler.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

@UtilityClass
public class QueueUtil {
    public <T> void reverse(Queue<T> queue) {
        Deque<T> deque = new ArrayDeque<>();
        while (!queue.isEmpty()) {
            deque.add(queue.poll());
        }
        while (!deque.isEmpty()) {
            queue.add(deque.pollLast());
        }
    }
}