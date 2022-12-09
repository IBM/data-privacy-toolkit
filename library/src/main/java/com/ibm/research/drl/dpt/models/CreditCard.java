/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

public class CreditCard {
    private final String name;
    private final String[] prefixes;
    private final int minimumLength;
    private final int maximumLength;

    /**
     * Instantiates a new Credit card.
     *
     * @param name          the name
     * @param prefixes      the prefixes
     * @param minimumLength the minimum length
     * @param maximumLength the maximum length
     */
    public CreditCard(String name, String[] prefixes, int minimumLength, int maximumLength) {
        this.name = name;
        this.prefixes = prefixes;
        this.minimumLength = minimumLength;
        this.maximumLength = maximumLength;
    }

    /**
     * Gets minimum length.
     *
     * @return the minimum length
     */
    public int getMinimumLength() {
        return minimumLength;
    }

    /**
     * Gets maximum length.
     *
     * @return the maximum length
     */
    public int getMaximumLength() {
        return maximumLength;
    }

    /**
     * Get prefixes string [ ].
     *
     * @return the string [ ]
     */
    public String[] getPrefixes() {
        return prefixes;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

}
