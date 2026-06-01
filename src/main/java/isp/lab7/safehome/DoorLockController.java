package isp.lab7.safehome;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DoorLockController implements ControllerInterface {
    private static final String OPERATION_ENTER_PIN = "enterPin";
    private static final String ERROR_INVALID_PIN = "Invalid pin";
    private static final String ERROR_TOO_MANY_ATTEMPTS = "Too many attempts";

    private final Map<Tenant, AccessKey> validAccess = new LinkedHashMap<>();
    private final List<AccessLog> accessLogs = new ArrayList<>();
    private final Door door = new Door();

    private int failedAttempts = 0;
    private boolean locked = false;

    @Override
    public DoorStatus enterPin(String pin) throws Exception {
        if (MASTER_KEY.equals(pin)) {
            locked = false;
            failedAttempts = 0;
            DoorStatus status = toggleDoor();
            accessLogs.add(new AccessLog(MASTER_TENANT_NAME, LocalDateTime.now(), OPERATION_ENTER_PIN, status, null));
            return status;
        }

        if (locked) {
            accessLogs.add(new AccessLog(resolveTenantName(pin), LocalDateTime.now(), OPERATION_ENTER_PIN, door.getStatus(),
                    ERROR_TOO_MANY_ATTEMPTS));
            throw new TooManyAttemptsException(ERROR_TOO_MANY_ATTEMPTS);
        }

        Tenant tenant = resolveTenantByPin(pin);
        if (tenant != null) {
            failedAttempts = 0;
            DoorStatus status = toggleDoor();
            accessLogs.add(new AccessLog(tenant.getName(), LocalDateTime.now(), OPERATION_ENTER_PIN, status, null));
            return status;
        }

        failedAttempts++;
        if (failedAttempts >= 3) {
            locked = true;
            accessLogs.add(new AccessLog(resolveTenantName(pin), LocalDateTime.now(), OPERATION_ENTER_PIN, door.getStatus(),
                    ERROR_TOO_MANY_ATTEMPTS));
            throw new TooManyAttemptsException(ERROR_TOO_MANY_ATTEMPTS);
        }

        accessLogs.add(new AccessLog(resolveTenantName(pin), LocalDateTime.now(), OPERATION_ENTER_PIN, door.getStatus(),
                ERROR_INVALID_PIN));
        throw new InvalidPinException(ERROR_INVALID_PIN);
    }

    @Override
    public void addTenant(String pin, String name) throws Exception {
        if (resolveTenantByName(name) != null) {
            throw new TenantAlreadyExistsException("Tenant already exists");
        }
        validAccess.put(new Tenant(name), new AccessKey(pin));
    }

    @Override
    public void removeTenant(String name) throws Exception {
        Tenant existingTenant = resolveTenantByName(name);
        if (existingTenant == null) {
            throw new TenantNotFoundException("Tenant not found");
        }
        validAccess.remove(existingTenant);
    }

    public List<AccessLog> getAccessLogs() {
        return new ArrayList<>(accessLogs);
    }

    private DoorStatus toggleDoor() {
        if (door.getStatus() == DoorStatus.CLOSE) {
            door.unlockDoor();
        } else {
            door.lockDoor();
        }
        return door.getStatus();
    }

    private Tenant resolveTenantByPin(String pin) {
        for (Map.Entry<Tenant, AccessKey> entry : validAccess.entrySet()) {
            if (entry.getValue().getPin().equals(pin)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private Tenant resolveTenantByName(String name) {
        for (Tenant tenant : validAccess.keySet()) {
            if (tenant.getName().equals(name)) {
                return tenant;
            }
        }
        return null;
    }

    private String resolveTenantName(String pin) {
        Tenant tenant = resolveTenantByPin(pin);
        return tenant != null ? tenant.getName() : "Unknown";
    }
}
