---
name: Serene Literary Narrative
colors:
  surface: '#f9f9fb'
  surface-dim: '#d9dadc'
  surface-bright: '#f9f9fb'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f3f5'
  surface-container: '#eeeef0'
  surface-container-high: '#e8e8ea'
  surface-container-highest: '#e2e2e4'
  on-surface: '#1a1c1d'
  on-surface-variant: '#41484a'
  inverse-surface: '#2f3132'
  inverse-on-surface: '#f0f0f2'
  outline: '#72787a'
  outline-variant: '#c1c7ca'
  surface-tint: '#45636b'
  primary: '#14333b'
  on-primary: '#ffffff'
  primary-container: '#2c4a52'
  on-primary-container: '#99b9c2'
  inverse-primary: '#acccd5'
  secondary: '#735c00'
  on-secondary: '#ffffff'
  secondary-container: '#fed65b'
  on-secondary-container: '#745c00'
  tertiary: '#452916'
  on-tertiary: '#ffffff'
  tertiary-container: '#5e3f2a'
  on-tertiary-container: '#d6ab90'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#c8e8f2'
  primary-fixed-dim: '#acccd5'
  on-primary-fixed: '#001f26'
  on-primary-fixed-variant: '#2d4b53'
  secondary-fixed: '#ffe088'
  secondary-fixed-dim: '#e9c349'
  on-secondary-fixed: '#241a00'
  on-secondary-fixed-variant: '#574500'
  tertiary-fixed: '#ffdcc7'
  tertiary-fixed-dim: '#eabda2'
  on-tertiary-fixed: '#2d1605'
  on-tertiary-fixed-variant: '#5f402b'
  background: '#f9f9fb'
  on-background: '#1a1c1d'
  surface-variant: '#e2e2e4'
  reading-indigo: '#3E4A89'
  serene-teal: '#2C4A52'
  progress-gold: '#D4AF37'
  sepia-bg: '#F4ECD8'
  sepia-text: '#5B4636'
  interface-gray: '#6E6E73'
typography:
  display-title:
    fontFamily: Source Serif 4
    fontSize: 34px
    fontWeight: '700'
    lineHeight: 41px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Source Serif 4
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 28px
  headline-lg-mobile:
    fontFamily: Source Serif 4
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 25px
  body-reading:
    fontFamily: Source Serif 4
    fontSize: 19px
    fontWeight: '400'
    lineHeight: 30px
  body-ui:
    fontFamily: Inter
    fontSize: 17px
    fontWeight: '400'
    lineHeight: 22px
  label-caps:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.05em
  callout:
    fontFamily: Inter
    fontSize: 15px
    fontWeight: '500'
    lineHeight: 20px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  margin-standard: 20px
  margin-compact: 16px
  gutter: 12px
  stack-sm: 8px
  stack-md: 16px
  stack-lg: 32px
---

## Brand & Style

This design system is built for a focused, premium reading experience that balances the utility of a productivity tool with the warmth of a physical library. The aesthetic is **Minimalist** with a **Tactile** edge, prioritizing high-legibility and a sense of "digital quiet." 

The interface should evoke a feeling of "Flow"—where the UI disappears to let the content lead. It draws inspiration from Apple’s Human Interface Guidelines but introduces softer depth and a specialized "Sepia" mode to reduce visual fatigue. The emotional response is one of calm accomplishment, treating reading not as a chore, but as a ritual.

## Colors

The palette is anchored by **Serene Teal**, a deep, grounded hue that provides enough contrast for interactive elements without being jarring. 

- **Primary:** Serene Teal is used for primary actions, active navigation states, and branding.
- **Secondary:** Progress Gold is reserved strictly for milestones, streaks, and "currently reading" progress indicators to provide a warm sense of reward.
- **Modes:** 
    - **Light:** Pure white backgrounds (#FFFFFF) with neutral gray secondary surfaces.
    - **Dark:** Deep charcoal (not pure black) to maintain soft contrast for OLED screens.
    - **Sepia:** A dedicated reading mode using a low-blue-light combination of `#F4ECD8` and `#5B4636` to mimic high-quality book paper.

## Typography

The typography system uses a dual-family approach to distinguish between "Content" and "Interface."

- **The Serif (Source Serif 4):** Used for book titles, quotes, and the primary reading experience. It provides the literary authority and warmth required for long-form immersion.
- **The Sans (Inter):** Used for navigation, buttons, labels, and metadata. It ensures the app feels like a modern, high-performance iOS tool.

**Key Rule:** Large titles (`display-title`) should always use negative letter spacing to feel "tighter" and more premium. Reading text (`body-reading`) requires a generous line height (approx 1.5x-1.6x) to prevent eye strain.

## Layout & Spacing

The layout follows a **Fixed-Margin Fluid** model. While the content scales to the width of the device, the horizontal margins remain strictly between 16pt and 20pt to align with iOS native behaviors.

- **Vertical Rhythm:** Use a 4px/8px base grid. Stacked elements (like a book list) should use `stack-md` (16px), while grouped content (like a title and its author) should use `stack-sm` (8px).
- **Safe Areas:** Adhere strictly to iOS Home Indicator and Status Bar safe areas.
- **Reading View:** The reading interface should introduce wider horizontal padding (up to 24pt) to create a focused column of text that doesn't feel cramped against the screen edges.

## Elevation & Depth

This design system uses **Tonal Layers** combined with **Ambient Shadows** to create a sense of organized calm.

- **Base Layer:** The primary background color (White, Dark, or Sepia).
- **Secondary Layer:** Slightly offset in tone (e.g., #F5F5F7 in Light mode) for grouped items like lists or card backgrounds.
- **Elevated Elements:** Book covers and primary action buttons utilize a very soft, diffused shadow (Blur: 20px, Y: 10px, Opacity: 8% of the primary color) to appear "lifted" off the page.
- **Backdrop Blurs:** Use iOS "Materials" (ultra-thin blurs) for Tab Bars and Navigation Bars to maintain context of the content scrolling beneath them.

## Shapes

The shape language is "Hyper-Rounded" to evoke a friendly, approachable, and modern feel.

- **Standard Components:** Buttons and Input fields use a base radius of **12px** (roundedness 2).
- **Feature Cards & Book Covers:** Use a large **20px+** radius (rounded-xl) to emphasize the premium, "squishy" tactile feel requested.
- **Progress Bars:** Should always be fully rounded (pill-shaped) to appear soft and continuous.

## Components

- **Buttons:** Primary buttons are large (50px+ height) with Serene Teal backgrounds and white text. Secondary buttons should be ghost-style with a subtle border or a tonal background.
- **Book Displays:** Covers are the hero. In grids, use a 2:3 aspect ratio with a subtle `1px` inner stroke to define edges on white backgrounds.
- **Progress Bars:** Thin, elegant bars using the `Progress Gold` color. Include a small percentage label in `label-caps` style above the bar.
- **Navigation:** Use SF Symbols for iconography. Icons should use the "Medium" weight to match the `Inter` UI font.
- **Lists:** Clean, borderless lists with 16px vertical padding and a subtle `0.5pt` separator that doesn't reach the full width of the screen.
- **Input Fields:** Soft-filled backgrounds with no border; the focus state should be indicated by a `2px` Serene Teal bottom border or a subtle outer glow.