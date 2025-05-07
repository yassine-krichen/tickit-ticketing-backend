package com.sofrecom.tickit.repository;

import com.sofrecom.tickit.model.ERole;
import com.sofrecom.tickit.model.Notification;
import com.sofrecom.tickit.model.NotificationType;
import com.sofrecom.tickit.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Repository for accessing and manipulating Notification entities.
 * Provides custom query methods beyond basic CRUD operations.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
    
    /**
     * Finds notifications by user reference.
     *
     * @param user The user to find notifications for
     * @param pageable Pagination information
     * @return A page of notifications for the specified user
     */
    Page<Notification> findByUserRef(User user, Pageable pageable);
    
    /**
     * Finds notifications by user reference and read flag.
     *
     * @param user The user to find notifications for
     * @param readFlag Whether the notifications have been read
     * @param pageable Pagination information
     * @return A page of notifications for the specified user with the given read status
     */
    Page<Notification> findByUserRefAndReadFlag(User user, boolean readFlag, Pageable pageable);
    
    /**
     * Finds notifications by user reference and notification type.
     *
     * @param user The user to find notifications for
     * @param type The type of notifications to find
     * @param pageable Pagination information
     * @return A page of notifications for the specified user of the given type
     */
    Page<Notification> findByUserRefAndType(User user, NotificationType type, Pageable pageable);
    
    /**
     * Finds notifications by user reference, read flag, and notification type.
     *
     * @param user The user to find notifications for
     * @param readFlag Whether the notifications have been read
     * @param type The type of notifications to find
     * @param pageable Pagination information
     * @return A page of notifications for the specified user with the given read status and type
     */
    Page<Notification> findByUserRefAndReadFlagAndType(User user, boolean readFlag, NotificationType type, Pageable pageable);
    
    /**
     * Finds notifications for users with a specific role.
     *
     * @param role The role to find notifications for
     * @param pageable Pagination information
     * @return A page of notifications targeting users with the specified role
     */
    @Query("SELECT n FROM Notification n JOIN n.target t WHERE t = :role")
    Page<Notification> findByTargetRole(@Param("role") ERole role, Pageable pageable);
    
    /**
     * Finds notifications for users with a specific role and read status.
     *
     * @param role The role to find notifications for
     * @param readFlag Whether the notifications have been read
     * @param pageable Pagination information
     * @return A page of notifications targeting users with the specified role and read status
     */
    @Query("SELECT n FROM Notification n JOIN n.target t WHERE t = :role AND n.readFlag = :readFlag")
    Page<Notification> findByTargetRoleAndReadFlag(@Param("role") ERole role, @Param("readFlag") boolean readFlag, Pageable pageable);
    
    /**
     * Marks multiple notifications as read.
     *
     * @param ids The IDs of the notifications to mark as read
     * @param readAt The timestamp when the notifications were read
     * @return The number of notifications updated
     */
    @Modifying
    @Query("UPDATE Notification n SET n.readFlag = true, n.readAt = :readAt WHERE n.id IN :ids AND n.readFlag = false")
    int markAsRead(@Param("ids") Collection<Long> ids, @Param("readAt") LocalDateTime readAt);
    
    /**
     * Counts unread notifications for a user.
     *
     * @param user The user to count unread notifications for
     * @return The number of unread notifications for the user
     */
    long countByUserRefAndReadFlagFalse(User user);
    
    /**
     * Finds notifications by their IDs and user.
     *
     * @param ids The IDs of the notifications to find
     * @param user The user the notifications belong to
     * @return The matching notifications
     */
    List<Notification> findByIdInAndUserRef(Collection<Long> ids, User user);
    
    /**
     * Deletes notifications by their IDs and user.
     *
     * @param ids The IDs of the notifications to delete
     * @param user The user the notifications belong to
     * @return The number of notifications deleted
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.id IN :ids AND n.userRef = :user")
    int deleteByIdInAndUserRef(@Param("ids") Collection<Long> ids, @Param("user") User user);
}