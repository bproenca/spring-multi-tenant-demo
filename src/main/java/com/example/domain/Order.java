package com.example.domain;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "orders")
public class Order {

    public Order() {
    }

    public Order(Date date) {
        this.date = date;
    }

    @Id
    @Column(nullable = false, name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false, name = "datex")
    private Date date;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Order other = (Order) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (id != other.id)
            return false;
        return true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Order [date=" + date + ", id=" + id + "]";
    }
}
