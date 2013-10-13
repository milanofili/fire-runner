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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author milano
 */
@Entity
@Table(name = "ways")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ways.findAll", query = "SELECT w FROM Ways w"),
    @NamedQuery(name = "Ways.findByWayid", query = "SELECT w FROM Ways w WHERE w.wayid = :wayid"),
    @NamedQuery(name = "Ways.findByLat", query = "SELECT w FROM Ways w WHERE w.lat = :lat"),
    @NamedQuery(name = "Ways.findByLng", query = "SELECT w FROM Ways w WHERE w.lng = :lng"),
    @NamedQuery(name = "Ways.findByAlt", query = "SELECT w FROM Ways w WHERE w.alt = :alt"),
    @NamedQuery(name = "Ways.findByUid", query = "SELECT w FROM Ways w WHERE w.uid = :uid"),
    @NamedQuery(name = "Ways.findByTimetag", query = "SELECT w FROM Ways w WHERE w.timetag = :timetag")})
public class Ways implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "wayid")
    private Integer wayid;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "lat")
    private Float lat;
    @Column(name = "lng")
    private Float lng;
    @Column(name = "alt")
    private Float alt;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "uid")
    private Integer uid;
    @Size(max = 10000)
    @Column(name = "timetag")
    private String timetag;

    public Ways() {
    }

    public Ways(Integer uid) {
        this.uid = uid;
    }

    public Integer getWayid() {
        return wayid;
    }

    public void setWayid(Integer wayid) {
        this.wayid = wayid;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    public Float getAlt() {
        return alt;
    }

    public void setAlt(Float alt) {
        this.alt = alt;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getTimetag() {
        return timetag;
    }

    public void setTimetag(String timetag) {
        this.timetag = timetag;
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
        if (!(object instanceof Ways)) {
            return false;
        }
        Ways other = (Ways) object;
        if ((this.uid == null && other.uid != null) || (this.uid != null && !this.uid.equals(other.uid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.sharif.ce.firerunnerbackends.Ways[ uid=" + uid + " ]";
    }
    
}
