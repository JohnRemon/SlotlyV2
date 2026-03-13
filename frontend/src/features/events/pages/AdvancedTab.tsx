import FormField from "@/components/common/FormField";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { cn } from "@/lib/utils";
import { Calendar, Copy, ExternalLink } from "lucide-react";
import { toast } from "sonner";

interface AdvancedTabProps {
    bookingUrl: string;
}

export const AdvancedTab = ({ bookingUrl }: AdvancedTabProps) => {
    return (
        <>
            <FormField
                label="Booking link"
                hint="Share this URL with attendees"
            >
                <div className="flex items-center gap-2">
                    <Input type="text" value={bookingUrl} readOnly />
                    <Button
                        type="button"
                        variant="outline"
                        size="icon"
                        aria-label="Copy link"
                        onClick={() => {
                            navigator.clipboard.writeText(bookingUrl);
                            toast.success("Link copied!");
                        }}
                    >
                        <Copy className="size-4" />
                    </Button>
                    <a
                        href={bookingUrl}
                        target="_blank"
                        rel="noreferrer"
                        className={cn(
                            "inline-flex",
                            "size-8 items-center justify-center",
                            "rounded-lg border border-border bg-background",
                            "transition-colors hover:bg-muted",
                            "focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/50",
                        )}
                        aria-label="Open booking page"
                    >
                        <ExternalLink className="size-4" />
                    </a>
                </div>
            </FormField>

            <div className="rounded-2xl border border-dashed bg-card/40 p-8 text-center">
                <Calendar className="mx-auto size-8 text-muted-foreground" />
                <div className="mt-3 text-sm font-medium">
                    Calendar sync coming soon
                </div>
                <div className="mt-1 text-xs text-muted-foreground">
                    Connect Google Calendar or Outlook to sync bookings
                    automatically.
                </div>
            </div>
        </>
    );
};
