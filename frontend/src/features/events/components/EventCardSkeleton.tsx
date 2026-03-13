export const EventCardSkeleton = () => (
    <div className="flex items-center gap-4 rounded-2xl border bg-card/60 px-4 py-3 shadow-sm ring-1 ring-foreground/5 animate-pulse">
        <div className="flex-1 space-y-2">
            <div className="h-4 w-1/3 rounded-lg bg-muted" />
            <div className="h-3 w-20 rounded-lg bg-muted" />
        </div>
        <div className="h-5 w-14 rounded-lg bg-muted" />
        <div className="hidden gap-1 sm:flex">
            <div className="size-7 rounded-lg bg-muted" />
            <div className="size-7 rounded-lg bg-muted" />
            <div className="size-7 rounded-lg bg-muted" />
        </div>
    </div>
);
