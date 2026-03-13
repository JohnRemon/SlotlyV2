import * as React from "react";
import { TriangleAlertIcon } from "lucide-react";

import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";

type ErrorMessageProps = {
    title?: string;
    message?: React.ReactNode;
    children?: React.ReactNode;
    onRetry?: () => void;
    retryLabel?: string;
    className?: string;
};

export default function ErrorMessage({
    title = "Something went wrong",
    message,
    children,
    onRetry,
    retryLabel = "Try again",
    className,
}: ErrorMessageProps) {
    return (
        <div
            role="alert"
            className={cn(
                "rounded-xl border border-destructive/30 bg-destructive/5 p-4 text-sm text-foreground",
                className,
            )}
        >
            <div className="flex items-start gap-3">
                <div className="mt-0.5 flex size-8 shrink-0 items-center justify-center rounded-lg bg-destructive/10 text-destructive">
                    <TriangleAlertIcon className="size-4" />
                </div>
                <div className="min-w-0 flex-1">
                    <div className="font-medium leading-snug">{title}</div>
                    {(message ?? children) && (
                        <div className="mt-1 text-muted-foreground">
                            {message ?? children}
                        </div>
                    )}
                    {onRetry && (
                        <div className="mt-3">
                            <Button
                                type="button"
                                variant="outline"
                                size="sm"
                                onClick={onRetry}
                            >
                                {retryLabel}
                            </Button>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}
