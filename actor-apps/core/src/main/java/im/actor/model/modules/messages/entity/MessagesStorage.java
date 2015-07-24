/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.model.modules.messages.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import im.actor.model.droidkit.bser.Bser;
import im.actor.model.droidkit.bser.BserObject;
import im.actor.model.droidkit.bser.BserValues;
import im.actor.model.droidkit.bser.BserWriter;

public class MessagesStorage extends BserObject {

    public static MessagesStorage fromBytes(byte[] data) throws IOException {
        return Bser.parse(new MessagesStorage(), data);
    }

    private ArrayList<MessageRef> messages = new ArrayList<MessageRef>();

    public ArrayList<MessageRef> getMessages() {
        return messages;
    }

    public void addOrUpdate(long rid, long date) {
        for (MessageRef ref : messages) {
            if (ref.getRid() == rid) {
                messages.remove(ref);
                break;
            }
        }
        messages.add(new MessageRef(rid, date));
    }

    public boolean update(long rid, long date) {
        for (MessageRef ref : messages) {
            if (ref.getRid() == rid) {
                messages.remove(ref);
                messages.add(new MessageRef(rid, date));
                return true;
            }
        }
        return false;
    }

    public boolean remove(long rid) {
        for (MessageRef ref : messages) {
            if (ref.getRid() == rid) {
                messages.remove(ref);
                return true;
            }
        }
        return false;
    }

    public ArrayList<MessageRef> removeBeforeDate(long date) {
        ArrayList<MessageRef> res = findBeforeDate(date);
        messages.removeAll(res);
        return res;
    }

    public ArrayList<MessageRef> findBeforeDate(long date) {
        ArrayList<MessageRef> res = new ArrayList<MessageRef>();
        for (MessageRef ref : messages) {
            if (ref.getDate() <= date) {
                res.add(ref);
            }
        }
        return res;
    }

    public int getCount() {
        return messages.size();
    }

    @Override

    public void parse(BserValues values) throws IOException {
        messages.clear();
        int count = values.getRepeatedCount(1);
        List<MessageRef> tmp = new ArrayList<MessageRef>();
        for (int i = 0; i < count; i++) {
            tmp.add(new MessageRef());
        }
        messages.addAll(values.getRepeatedObj(1, tmp));
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedObj(1, messages);
    }
}
