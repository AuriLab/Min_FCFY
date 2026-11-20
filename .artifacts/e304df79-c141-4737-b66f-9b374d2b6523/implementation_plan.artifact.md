# Implementation Plan - Add "Banks" Max P/TBV Calculator & Navigation

We will add a second calculator page specifically for financial institutions (Banks) to calculate **Maximum Targeted Price to Tangible Book Value (Max P/TBV)** using the Excess Return (Residual Income) model, accessible via a "Banks" button on the homescreen.

## User Review Required

> [!IMPORTANT]
> **Navigation Design:** We will add a toggle/button in the Top Bar ("Banks" / "FCFY") to switch seamlessly between the Minimum FCFY Calculator and the new Max P/TBV Calculator for Banks.

> [!IMPORTANT]
> **Valuation Formula for Banks (Residual Income / Excess Return Model):**
> $\text{Max P/TBV} = 1 + (ROE - R) \times (A1 + A2 + A3)$
> - **$A1$**: Present Value of excess returns over Years 1–5 compounding at growth rate $g$.
> - **$A2$**: Present Value of excess returns over Years 6–10 compounding at growth rate $h$ off the book value built up in Phase 1 ($BV_5$).
> - **$A3$**: Present Value of terminal excess returns (Year 11+) compounding at terminal growth rate $t$.

## Proposed Changes

### [app] [MainActivity.kt](file:///C:/Users/koayf/AndroidStudioProjects/MinFCFYield/app/src/main/java/com/example/fcfycalculator/MainActivity.kt)

- **Navigation State:** Add a screen state (`enum class CalculatorScreen { Fcfy, Banks }`) and a top bar button to toggle between FCFY Calculator and Banks Max P/TBV Calculator.
- **Banks Calculation Engine (`BanksCalculatorEngine`):**
  - Implement calculation of $A1$, $A2$, $A3$, and Max P/TBV given parameters: ROE, Cost of Equity ($R$), Phase 1 Growth ($g$), Phase 2 Growth ($h$), and Terminal Growth ($t$).
- **Banks Calculator Screen UI (`BanksCalculatorScreen`):**
  - Sliders for ROE, Target Cost of Equity ($R$), Phase 1 Growth ($g$), Phase 2 Growth ($h$), and Terminal Growth ($t$).
  - Hero result card displaying Maximum Targeted P/TBV, breakdown of $A1$, $A2$, $A3$, and component contributions.
  - Explanation note.

## Verification Plan

### Automated Tests
- Run `:app:assembleDebug` to verify compilation.

### Manual Verification
- Deploy to emulator and test switching between FCFY and Banks calculators, adjusting sliders, and verifying calculation results.
