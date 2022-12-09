/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.LatitudeLongitude;
import com.ibm.research.drl.dpt.providers.identifiers.LatitudeLongitudeIdentifier;
import com.ibm.research.drl.dpt.util.RandomGenerators;

import java.security.SecureRandom;

/**
 * The type Latitude longitude masking provider.
 *
 * @author santonat
 */


public class LatitudeLongitudeMaskingProvider extends AbstractMaskingProvider {

    private final boolean fixedRadiusRandomDirection;
    private final boolean donutMasking;
    private final boolean randomWithinCircle;

    // TODO: implement NRand and theta rand: http://geomobile.como.polimi.it/website/presentations/Location%20Privacy%20Polimi.pdf
    //private final boolean nrand;
    //private final boolean thetaRand;

    //TODO : digit reduction: http://geomobile.como.polimi.it/website/presentations/Location%20Privacy%20Polimi.pdf
    // private final boolean digitReduction
    // private final int digitsToReduct

    private final static int minimumOffset = 10;

    private final int maximumOffsetRadius;
    private final int minimumOffsetRadius;

    private final SecureRandom random;
    private final LatitudeLongitudeIdentifier latitudeLongitudeIdentifier = new LatitudeLongitudeIdentifier();

    public LatitudeLongitudeMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    public LatitudeLongitudeMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this(new SecureRandom(), maskingConfiguration);
    }
    /**
     * Instantiates a new Latitude longitude masking provider.
     *
     * @param configuration the configuration
     */
    public LatitudeLongitudeMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {

        this.randomWithinCircle = configuration.getBooleanValue("latlon.mask.randomWithinCircle");
        this.donutMasking = configuration.getBooleanValue("latlon.mask.donutMasking");
        this.fixedRadiusRandomDirection = configuration.getBooleanValue("latlon.mask.fixedRadiusRandomDirection");

        this.minimumOffsetRadius = configuration.getIntValue("latlon.offset.minimumRadius");
        this.maximumOffsetRadius = configuration.getIntValue("latlon.offset.maximumRadius");

        if (this.maximumOffsetRadius <= minimumOffset) {
            throw new IllegalArgumentException("invalid maximum offset radius:" + this.maximumOffsetRadius);
        }

        if(this.minimumOffsetRadius <= minimumOffset) {
            throw new IllegalArgumentException("invalid minimum offset radius:" + this.minimumOffsetRadius);
        }

        this.random = random;
    }

    /**
     * Instantiates a new Latitude longitude masking provider.
     */
    public LatitudeLongitudeMaskingProvider()
    {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    public String mask(LatitudeLongitude latitudeLongitude) {
        LatitudeLongitude randomLatLon;

        if (this.randomWithinCircle) {
            randomLatLon = RandomGenerators.generateRandomCoordinate(latitudeLongitude, this.maximumOffsetRadius);
        }
        else if (this.donutMasking) {
            randomLatLon = RandomGenerators.generateRandomCoordinate(latitudeLongitude, this.minimumOffsetRadius, this.maximumOffsetRadius);
        }
        else  if (this.fixedRadiusRandomDirection) {
            randomLatLon = RandomGenerators.generateRandomCoordinateRandomDirection(latitudeLongitude, this.maximumOffsetRadius);
        }
        else {
            randomLatLon = RandomGenerators.generateRandomCoordinate();
        }

        return randomLatLon.toString();
    }

    @Override
    public String mask(String identifier)
    {
        LatitudeLongitude latitudeLongitude = latitudeLongitudeIdentifier.parseCoordinate(identifier);
        if (latitudeLongitude == null) {
            return RandomGenerators.generateRandomCoordinate().toString();
        }

        return mask(latitudeLongitude);
    }
}
