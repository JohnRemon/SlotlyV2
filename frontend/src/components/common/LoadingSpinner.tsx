import { Loader2Icon } from "lucide-react";

import { cn } from "@/lib/utils";

type LoadingSpinnerProps = {
    label?: string;
    className?: string;
    size?: "xs" | "sm" | "md" | "lg";
};

const sizeClasses: Record<NonNullable<LoadingSpinnerProps["size"]>, string> = {
    xs: "size-3",
    sm: "size-4",
    md: "size-5",
    lg: "size-6",
};

export default function LoadingSpinner({
    label = "Loading",
    className,
    size = "md",
}: LoadingSpinnerProps) {
    return (
        <div
            role="status"
            aria-label={label}
            className={cn(
                "inline-flex items-center gap-2 text-sm text-muted-foreground",
                className,
            )}
        >
            <Loader2Icon className={cn("animate-spin", sizeClasses[size])} />
            <span className="sr-only">{label}</span>
        </div>
    );
}
