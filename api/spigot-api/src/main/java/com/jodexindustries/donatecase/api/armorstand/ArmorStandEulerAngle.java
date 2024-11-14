package com.jodexindustries.donatecase.api.armorstand;

import org.bukkit.util.EulerAngle;

public class ArmorStandEulerAngle {
    public ArmorStandEulerAngle(EulerAngle head, EulerAngle body, EulerAngle rightArm, EulerAngle leftArm, EulerAngle rightLeg, EulerAngle leftLeg) {
        this.head = head;
        this.body = body;
        this.rightArm = rightArm;
        this.leftArm = leftArm;
        this.rightLeg = rightLeg;
        this.leftLeg = leftLeg;
    }

    private EulerAngle head;
    private EulerAngle body;
    private EulerAngle rightArm;
    private EulerAngle leftArm;
    private EulerAngle rightLeg;
    private EulerAngle leftLeg;

    public EulerAngle getHead() {
        return head;
    }

    public void setHead(EulerAngle head) {
        this.head = head;
    }

    public EulerAngle getBody() {
        return body;
    }

    public void setBody(EulerAngle body) {
        this.body = body;
    }

    public EulerAngle getRightArm() {
        return rightArm;
    }

    public void setRightArm(EulerAngle rightArm) {
        this.rightArm = rightArm;
    }

    public EulerAngle getLeftArm() {
        return leftArm;
    }

    public void setLeftArm(EulerAngle leftArm) {
        this.leftArm = leftArm;
    }

    public EulerAngle getRightLeg() {
        return rightLeg;
    }

    public void setRightLeg(EulerAngle rightLeg) {
        this.rightLeg = rightLeg;
    }

    public EulerAngle getLeftLeg() {
        return leftLeg;
    }

    public void setLeftLeg(EulerAngle leftLeg) {
        this.leftLeg = leftLeg;
    }
}
