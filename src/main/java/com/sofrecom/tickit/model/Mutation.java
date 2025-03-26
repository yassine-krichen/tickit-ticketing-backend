package com.sofrecom.tickit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Mutation  {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    private String agent;
    private String cuid;

    @Temporal(TemporalType.DATE)
    private Date dateTraitement;

    @OneToOne(cascade = CascadeType.ALL)
    private NATHD nathd;
    @OneToOne(cascade = CascadeType.ALL)
    private NATNE natne;
    @OneToOne(cascade = CascadeType.ALL)
    private NAT42C nat42C;





}
