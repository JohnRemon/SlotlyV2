import * as React from "react";

import { cn } from "@/lib/utils";

type PageHeaderProps = {
    title: React.ReactNode;
    description?: React.ReactNode;
    actions?: React.ReactNode;
    className?: string;
};

export default function PageHeader({
    title,
    description,
    actions,
    className,
}: PageHeaderProps) {
    return (
        <header
            className={cn(
                "flex flex-col gap-3 rounded-2xl border bg-card/60 p-5 shadow-sm ring-1 ring-foreground/5 supports-backdrop-filter:backdrop-blur-sm sm:flex-row sm:items-start sm:justify-between",
                className,
            )}
        >
            <div className="min-w-0">
                <h1 className="text-lg font-semibold leading-tight tracking-[-0.01em] sm:text-xl">
                    {title}
                </h1>
                {description && (
                    <p className="mt-1 text-sm text-muted-foreground">
                        {description}
                    </p>
                )}
            </div>
            {actions && (
                <div className="flex shrink-0 items-center gap-2 sm:mt-0">
                    {actions}
                </div>
            )}
        </header>
    );
}
