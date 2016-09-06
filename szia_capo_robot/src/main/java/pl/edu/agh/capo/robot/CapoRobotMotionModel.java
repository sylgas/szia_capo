package pl.edu.agh.capo.robot;


import pl.edu.agh.capo.common.Location;
import pl.edu.agh.capo.maze.Coordinates;

public class CapoRobotMotionModel {

    private Location location;
    private double velocityLeft;
    private double velocityRight;
    private double accelerationLeft;
    private double accelerationRight;
    private boolean isRandom;

    public CapoRobotMotionModel(Coordinates coordinates, double angle) {
        this.location = new Location(coordinates, angle);
    }

    /**
     * First calculate the Center of the circle:
     * arcCenter = [x − R sin(θ) , y + R cos(θ)]
     * Then calculate new location:
     * rotating a distance R about its ICC with an angular velocity of ω.
     * <p>
     * http://chess.eecs.berkeley.edu/eecs149/documentation/differentialDrive.pdf
     */
    public Location getLocationAfterTime(Measure measure, double deltaTimeInMillis) {
        double velocityRight = measure.getRightVelocity();
        double velocityLeft = measure.getLeftVelocity();
        double deltaTime = deltaTimeInMillis / 1000.0;

        if (velocitiesNeedCorrection(velocityLeft, velocityRight, deltaTime)) {
            velocityRight = this.velocityRight + accelerationRight * deltaTime;
            velocityLeft = this.velocityLeft + accelerationLeft * deltaTime;
        }

        if (velocityLeft == velocityRight) {
            return calculateLocation(velocityRight, velocityLeft, deltaTime);
        }
        return calculateLocationWithDirectionChange(velocityRight, velocityLeft, deltaTime);
    }

    public void applyLocation(Location location, Measure measure, double deltaTimeInMillis) {
        double deltaTime = deltaTimeInMillis / 1000.0;
        this.location = location;
        this.accelerationLeft = calculateAcceleration(velocityLeft, measure.getLeftVelocity(), deltaTime);
        this.accelerationRight = calculateAcceleration(velocityRight, measure.getRightVelocity(), deltaTime);
        this.velocityRight = measure.getRightVelocity();
        this.velocityLeft = measure.getLeftVelocity();
        this.isRandom = false;
    }

    public void applyLocation(Location location) {
        this.location = location;
        this.isRandom = true;
    }

    private Location calculateLocationWithDirectionChange(double velocityRight, double velocityLeft, double deltaTime) {
        double radius = CapoRobotConstants.WHEELS_HALF_DISTANCE * (velocityLeft + velocityRight) / (velocityLeft - velocityRight);
        double directionInRadians = getAdjustedDirectionToRadians();

        double arcCenterX = location.positionX - radius * Math.sin(directionInRadians);
        double arcCenterY = location.positionY + radius * Math.cos(directionInRadians);

        double angularVelocityDeltaTime = getAngularVelocity(velocityRight, velocityLeft) * deltaTime;

        double newX = Math.cos(angularVelocityDeltaTime) * (location.positionX - arcCenterX)
                - Math.sin(angularVelocityDeltaTime) * (location.positionY - arcCenterY)
                + arcCenterX;
        double newY = Math.sin(angularVelocityDeltaTime) * (location.positionX - arcCenterX)
                + Math.cos(angularVelocityDeltaTime) * (location.positionY - arcCenterY)
                + arcCenterY;

        return new Location(newX, newY, Math.toDegrees(directionInRadians + angularVelocityDeltaTime));
    }

    /**
     * Direction is adjusted to motionmodel
     */
    private double getAdjustedDirectionToRadians() {
        return Math.toRadians(location.alpha - 90);
    }

    private Location calculateLocation(double velocityRight, double velocityLeft, double deltaTime) {
        double linearVelocity = getLinearVelocity(velocityRight, velocityLeft);
        double x = location.positionX + linearVelocity * Math.cos(getAdjustedDirectionToRadians() * deltaTime);
        double y = location.positionY + linearVelocity * Math.sin(getAdjustedDirectionToRadians() * deltaTime);
        return new Location(x, y, location.alpha);
    }

    private double getLinearVelocity(double velocityRight, double velocityLeft) {
        return (velocityLeft + velocityRight) / 2;
    }

    private double getLinearVelocity() {
        return getLinearVelocity(velocityRight, velocityLeft);
    }

    private boolean velocityExceedMax(double linearVelocity) {
        return Math.abs(linearVelocity) > CapoRobotConstants.MAX_LINEAR_VELOCITY;
    }

    private boolean accelerationExceedMax(double linearVelocity, double deltaTime) {
        double acceleration = calculateAcceleration(getLinearVelocity(), linearVelocity, deltaTime);
        return !isRandom && Math.abs(acceleration) > CapoRobotConstants.MAX_ACCELERATION;
    }

    private boolean velocitiesNeedCorrection(double velocityRight, double velocityLeft, double deltaTime) {
        double linearVelocity = getLinearVelocity(velocityLeft, velocityRight);
        return velocityExceedMax(linearVelocity) || accelerationExceedMax(linearVelocity, deltaTime);
    }

    private double getAngularVelocity(double velocityRight, double velocityLeft) {
        return (velocityLeft - velocityRight) / (2 * CapoRobotConstants.WHEELS_HALF_DISTANCE);
    }

    private double calculateAcceleration(double V1, double V2, double deltaTime) {
        return (V2 - V1) / deltaTime;
    }

    public Location getLocation() {
        return location;
    }
}

