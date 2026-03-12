import * as React from "react"

import { cn } from "@/lib/utils"

type SwitchProps = Omit<React.ComponentPropsWithoutRef<"button">, "onChange"> & {
  checked?: boolean
  defaultChecked?: boolean
  onCheckedChange?: (checked: boolean) => void
}

const Switch = React.forwardRef<HTMLButtonElement, SwitchProps>(
  (
    {
      checked,
      defaultChecked = false,
      onCheckedChange,
      disabled,
      className,
      onClick,
      ...props
    },
    ref
  ) => {
    const [internalChecked, setInternalChecked] = React.useState(defaultChecked)
    const isControlled = checked !== undefined
    const currentChecked = isControlled ? checked : internalChecked

    const setChecked = (next: boolean) => {
      if (!isControlled) setInternalChecked(next)
      onCheckedChange?.(next)
    }

    return (
      <button
        ref={ref}
        type="button"
        role="switch"
        aria-checked={currentChecked}
        data-state={currentChecked ? "checked" : "unchecked"}
        disabled={disabled}
        onClick={(e) => {
          onClick?.(e)
          if (e.defaultPrevented) return
          setChecked(!currentChecked)
        }}
        className={cn(
          "group inline-flex h-5 w-9 shrink-0 cursor-pointer items-center rounded-full border border-input bg-input/20 p-0.5 transition-colors outline-none focus-visible:ring-3 focus-visible:ring-ring/50 disabled:cursor-not-allowed disabled:opacity-50 data-[state=checked]:border-primary/25 data-[state=checked]:bg-primary",
          className
        )}
        {...props}
      >
        <span
          className={cn(
            "pointer-events-none size-4 rounded-full bg-background shadow-sm transition-transform group-data-[state=checked]:translate-x-4"
          )}
        />
      </button>
    )
  }
)

Switch.displayName = "Switch"

export { Switch }
