/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sharif.ce.firerunnerbackends;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author milano
 */
@Entity
@Table(name = "user")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username"),
    @NamedQuery(name = "User.findByPassword", query = "SELECT u FROM User u WHERE u.password = :password"),
    @NamedQuery(name = "User.findByUid", query = "SELECT u FROM User u WHERE u.uid = :uid")})
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Column(name = "username")
    private Serializable username;
    @Column(name = "password")
    private Serializable password;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "uid")
    private Integer uid;

    public User() {
    }

    public User(Integer uid) {
        this.uid = uid;
    }

    public User(Integer uid, Serializable username) {
        this.uid = uid;
        this.username = username;
    }

    public Serializable getUsername() {
        return username;
    }

    public void setUsername(Serializable username) {
        this.username = username;
    }

    public Serializable getPassword() {
        return password;
    }

    public void setPassword(Serializable password) {
        this.password = password;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uid != null ? uid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.uid == null && other.uid != null) || (this.uid != null && !this.uid.equals(other.uid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.sharif.ce.firerunnerbackends.User[ uid=" + uid + " ]";
    }
    
}
