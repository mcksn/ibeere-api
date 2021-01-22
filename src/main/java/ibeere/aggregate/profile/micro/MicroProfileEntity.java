package ibeere.aggregate.profile.micro;

import org.springframework.beans.factory.annotation.Autowired;
import ibeere.event.EventPublisher;
import ibeere.aggregate.profile.AbstractProfileEntity;
import ibeere.aggregate.profile.QandAStatus;
import ibeere.user.UserId;

import javax.persistence.*;

import static org.springframework.web.context.support.SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext;

@Entity
@Table(name = "profile_entity")
@EntityListeners(MicroProfileEntity.Listener.class)
public class MicroProfileEntity extends AbstractProfileEntity {

    @Column()
    private String imgUrl;

    private String name;

    @Column()
    private String lastName;

    @Column(unique = true)
    private String path;

    private boolean verified;

    @Enumerated(EnumType.STRING)
    private QandAStatus qandAStatus;

    public MicroProfileEntity(UserId id, String imgUrl, FullName name) {
        super(id);
        this.imgUrl = imgUrl;
        this.name = name.first();
        this.lastName = name.last();
        this.path = generatePath(id, name);
        this.verified = false;
    }

    // hibernate
    public MicroProfileEntity() {
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void changeImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public FullName getName() {
        return FullName.of(name, lastName);
    }

    public String getPath() {
        return path;
    }

    public void updateName(FullName name) {
        this.name = name.first();
        this.lastName = name.last();
        this.path = generatePath(this.getId(), name);
    }

    String generatePath(UserId userId, FullName name) {
        return name.full().trim()
                .replaceAll("[^A-Za-z0-9]", "-")
                .concat("-")
                .concat(userId.getId().toString().substring(0, 3));
    }

    public QandAStatus getQandAStatus() {
        return qandAStatus;
    }

    public boolean isDoingAQandA() {
        return qandAStatus != null;
    }

    public void updateQandAStatus(QandAStatus qandAStatus) {
        this.qandAStatus = qandAStatus;
    }

    public boolean isVerified() {
        return verified;
    }

    static class Listener {
        @Autowired
        private EventPublisher eventPublisher;

        @PrePersist void onPrePersist(Object o) {}
        @PostPersist void onPostPersist(MicroProfileEntity microProfileEntity) {
            processInjectionBasedOnCurrentContext(this);
            eventPublisher.microProfileUpdated(microProfileEntity.getId());
        }
        @PostLoad void onPostLoad(Object o) {}
        @PreUpdate void onPreUpdate(Object o) {}
        @PostUpdate void onPostUpdate(MicroProfileEntity microProfileEntity) {
            processInjectionBasedOnCurrentContext(this);
            eventPublisher.microProfileUpdated(microProfileEntity.getId());
        }
        @PreRemove void onPreRemove(Object o) {}
        @PostRemove void onPostRemove(Object o) {}
    }
}
