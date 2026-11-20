# Walkthrough - Add "Banks" Max P/TBV Calculator & Navigation

We have successfully implemented the Maximum Targeted Price to Tangible Book Value (Max P/TBV) calculator for financial institutions (Banks), integrated a top bar navigation toggle, and verified the build and execution.

## Changes Made

### [app] [MainActivity.kt](file:///C:/Users/koayf/AndroidStudioProjects/MinFCFYield/app/src/main/java/com/example/fcfycalculator/MainActivity.kt)

- **Navigation & Screen State:** Added `CalculatorScreen` (`Fcfy` vs `Banks`) and a Top Bar button ("Banks" / "FCFY") enabling seamless switching between calculators.
- **Banks Calculation Engine (`BanksCalculationEngine`):**
  - Implemented the Excess Return / Residual Income model formula: $\text{Max P/TBV} = 1 + (ROE - R) \times (A1 + A2 + A3)$.
  - Calculated $A1$ (Years 1–5 excess returns factor), $A2$ (Years 6–10 excess returns factor), and $A3$ (Terminal excess returns factor).
- **Banks Calculator Screen UI (`BanksCalculatorScreen`):**
  - Sliders with `-` and `+` steppers for ROE, Cost of Equity ($R$), Phase 1 Growth ($g$), Phase 2 Growth ($h$), and Terminal Growth ($t$).
  - Hero card displaying Max P/TBV, ROE & Cost of Equity subtitle, and $A1$, $A2$, $A3$ factor breakdowns.
  - Informational note for bank valuation assumptions.

## Verification Results

### Automated Tests
- Gradle build task `:app:assembleDebug` completed successfully with zero compilation errors.

### Manual Verification
- Deployed successfully to Android emulator (`emulator-5554`) with zero runtime errors.
- Verified top bar button toggle and interactivity of sliders on both screens.
