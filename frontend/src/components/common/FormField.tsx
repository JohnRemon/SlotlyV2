import type { ReactNode } from "react";

import { Label } from "@/components/ui/label";
import { cn } from "@/lib/utils";

type FormFieldProps = {
    id?: string;
    label: ReactNode;
    hint?: ReactNode;
    required?: boolean;
    className?: string;
    children: ReactNode;
};

export default function FormField({
    id,
    label,
    hint,
    required = false,
    className,
    children,
}: FormFieldProps) {
    return (
        <div className={cn("grid gap-2", className)}>
            <div className="flex items-center gap-1.5">
                <Label htmlFor={id}>{label}</Label>
                {required && (
                    <span aria-hidden="true" className="text-destructive">
                        *
                    </span>
                )}
            </div>
            {children}
            {hint && <p className="text-xs text-muted-foreground">{hint}</p>}
        </div>
    );
}
