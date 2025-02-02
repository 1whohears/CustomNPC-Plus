package noppes.npcs.scripted.interfaces.handler;

import noppes.npcs.scripted.interfaces.handler.data.IQuest;

public interface IPlayerQuestData {

    IQuest getTrackedQuest();

    void startQuest(int id);

    void finishQuest(int id);

    void stopQuest(int id);

    void removeQuest(int id);

    boolean hasFinishedQuest(int id);

    public boolean hasActiveQuest(int id);

    IQuest[] getActiveQuests();

    IQuest[] getFinishedQuests();
}
