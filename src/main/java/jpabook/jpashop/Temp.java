package jpabook.jpashop;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "temp")
public class Temp {
    @Id
    @Column(name = "temp1", nullable = false)
    private Long temp1;

    @Column(name = "temp2")
    private String temp2;

    public String getTemp2() {
        return temp2;
    }

    public void setTemp2(String temp2) {
        this.temp2 = temp2;
    }

    public Long getTemp1() {
        return temp1;
    }

    public void setTemp1(Long temp1) {
        this.temp1 = temp1;
    }
}