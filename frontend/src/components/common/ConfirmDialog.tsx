import * as React from "react";
import type { VariantProps } from "class-variance-authority";

import { Button, buttonVariants } from "@/components/ui/button";
import {
    Dialog,
    DialogClose,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog";

type ConfirmDialogProps = {
    trigger?: React.ReactElement;
    open?: boolean;
    onOpenChange?: (open: boolean) => void;
    title: React.ReactNode;
    description?: React.ReactNode;
    confirmLabel?: string;
    cancelLabel?: string;
    confirmVariant?: VariantProps<typeof buttonVariants>["variant"];
    onConfirm: () => void | Promise<void>;
    disableConfirm?: boolean;
};

export default function ConfirmDialog({
    trigger,
    open,
    onOpenChange,
    title,
    description,
    confirmLabel = "Confirm",
    cancelLabel = "Cancel",
    confirmVariant = "destructive",
    onConfirm,
    disableConfirm,
}: ConfirmDialogProps) {
    const [busy, setBusy] = React.useState(false);
    const [internalOpen, setInternalOpen] = React.useState(false);

    const isControlled = open !== undefined;
    const currentOpen = isControlled ? open : internalOpen;
    const handleOpenChange = (nextOpen: boolean) => {
        if (!isControlled) setInternalOpen(nextOpen);
        onOpenChange?.(nextOpen);
    };

    const handleConfirm = async () => {
        try {
            setBusy(true);
            await onConfirm();
            handleOpenChange(false);
        } finally {
            setBusy(false);
        }
    };

    return (
        <Dialog open={currentOpen} onOpenChange={handleOpenChange}>
            {trigger && <DialogTrigger asChild>{trigger}</DialogTrigger>}
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>{title}</DialogTitle>
                    {description && (
                        <DialogDescription>{description}</DialogDescription>
                    )}
                </DialogHeader>
                <DialogFooter>
                    <DialogClose asChild>
                        <Button variant="outline">{cancelLabel}</Button>
                    </DialogClose>
                    <Button
                        type="button"
                        variant={confirmVariant}
                        onClick={handleConfirm}
                        disabled={busy || disableConfirm}
                    >
                        {busy ? "Working..." : confirmLabel}
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}
