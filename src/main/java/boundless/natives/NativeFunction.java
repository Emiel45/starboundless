package boundless.natives;

public class NativeFunction {
    private NativeClass parent;
    private long address;
    private String name;

    private int hashCode;

    public NativeFunction(NativeClass parent, long address, String name) {
        this.parent = parent;
        this.address = address;
        this.name = name;

        this.hashCode = (int) (address ^ (address >>> 32));
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    @Override
    public String toString() {
        return "[0x" + Long.toHexString(address) + "] " + parent.getName() + "::" + name;
    }

    public NativeClass getParent() {
        return parent;
    }

    public long getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }
}
