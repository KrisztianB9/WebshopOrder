package ro.umfst.oop;

public abstract class BaseEntity {

    protected String id;

    public BaseEntity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public abstract String businessKey();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BaseEntity that = (BaseEntity) obj;

        String thisKey = this.businessKey();
        String thatKey = that.businessKey();

        return (thisKey != null) ? thisKey.equals(thatKey) : (thatKey == null);
    }

    @Override
    public int hashCode() {
        String key = this.businessKey();
        return (key != null) ? key.hashCode() : 0;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[id=" + id + ", key=" + businessKey() + "]";
    }
}


