package org.ironmaple.simulation.drivesims;

public class COTS {
    /**
     *
     *
     * <h2>Stores the coefficient of friction of some common used wheels.</h2>
     *
     * <p>Data comes from <a href='https://www.chiefdelphi.com/t/spectrum-3847-build-blog-2024/447471/217'>Spectrum
     * 3847's Build Blog</a>.
     */
    public enum WHEELS {
        /** <a href='https://www.vexrobotics.com/colsonperforma.html'>Colsons Wheels.</a> */
        COLSONS(0.899),
        /**
         * Default Neoprene Treads for <a
         * href='https://www.swervedrivespecialties.com/products/mk4i-swerve-module'>Mark4 Modules</a>
         */
        DEFAULT_NEOPRENE_TREAD(1.426),
        /**
         * <a href='https://www.andymark.com/products/blue-nitrile-roughtop-tread-1-in-wide-10-ft-long'>Blue Nitrile
         * Tread from AndyMark.</a>
         */
        BLUE_NITRILE_TREAD(1.542),
        /** <a href='https://www.vexrobotics.com/217-9064.html'>Vex Grip V2 Wheel.</a> */
        VEX_GRIP_V2(1.916),
        /**
         * <a href='https://www.thebluealliance.com/team/88'>Team 88</a>'s <a
         * href='https://www.chiefdelphi.com/t/tpu90a-grippy-tire-cad-published-finally/438075'>TPU90A Grippy Tire</a>
         */
        SLS_PRINTED_WHEELS(2.106);

        public final double cof;

        WHEELS(double cof) {
            this.cof = cof;
        }
    }
}
