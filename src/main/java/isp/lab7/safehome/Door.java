package isp.lab7.safehome;

public class Door {
    private DoorStatus status = DoorStatus.CLOSE;

    public DoorStatus getStatus() {
        return status;
    }

    public void lockDoor() {
        status = DoorStatus.CLOSE;
    }

    public void unlockDoor() {
        status = DoorStatus.OPEN;
    }
}
