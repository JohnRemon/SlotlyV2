import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
    Select,
    SelectContent,
    SelectGroup,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";
import { useAuth } from "@/features/auth/hooks/useAuth";
import { useApiError } from "@/hooks/useApiError";
import { zodResolver } from "@hookform/resolvers/zod";
import { Loader2Icon } from "lucide-react";
import { useEffect } from "react";
import { useForm, useWatch } from "react-hook-form";
import { toast } from "sonner";
import * as z from "zod";
import {
    useUpdateFirstName,
    useUpdateLastName,
    useUpdateTimeZone,
} from "../hooks/useUsers";

const profileSchema = z.object({
    firstName: z
        .string()
        .min(1, "First name is required.")
        .max(32, "First name must be at most 32 characters."),
    lastName: z
        .string()
        .min(1, "Last name is required.")
        .max(32, "Last name must be at most 32 characters."),
    timeZone: z.string().min(1, "Timezone is required."),
});

type ProfileFormData = z.infer<typeof profileSchema>;

const timeZones = Intl.supportedValuesOf("timeZone");

const ProfileTab = () => {
    const { user } = useAuth();
    const handleError = useApiError();

    const updateFirstName = useUpdateFirstName();
    const updateLastName = useUpdateLastName();
    const updateTimeZone = useUpdateTimeZone();

    const {
        register,
        handleSubmit,
        reset,
        control,
        setValue,
        formState: { errors, dirtyFields },
    } = useForm<ProfileFormData>({
        resolver: zodResolver(profileSchema),
        defaultValues: {
            firstName: user?.firstName ?? "",
            lastName: user?.lastName ?? "",
            timeZone: user?.timeZone ?? "",
        },
    });

    const timeZone = useWatch({ control, name: "timeZone" });

    useEffect(() => {
        if (!user) return;
        reset({
            firstName: user.firstName,
            lastName: user.lastName,
            timeZone: user.timeZone,
        });
    }, [user, reset]);

    const onSubmit = async (data: ProfileFormData) => {
        if (!user) return;
        try {
            const promises = [];
            if (dirtyFields.firstName)
                promises.push(
                    updateFirstName.mutateAsync({
                        id: user.id,
                        firstName: data.firstName,
                    }),
                );
            if (dirtyFields.lastName)
                promises.push(
                    updateLastName.mutateAsync({
                        id: user.id,
                        lastName: data.lastName,
                    }),
                );
            if (dirtyFields.timeZone)
                promises.push(
                    updateTimeZone.mutateAsync({
                        id: user.id,
                        timeZone: data.timeZone,
                    }),
                );
            if (promises.length === 0) return;
            await Promise.all(promises);
            toast.success("Profile updated.");
        } catch (error) {
            handleError(error);
        }
    };

    const isLoading =
        updateFirstName.isPending ||
        updateLastName.isPending ||
        updateTimeZone.isPending;

    return (
        <Card className="bg-card/60 supports-backdrop-filter:backdrop-blur-sm">
            <CardHeader className="border-b">
                <CardTitle className="text-base">Profile</CardTitle>
            </CardHeader>
            <CardContent>
                <form onSubmit={handleSubmit(onSubmit)} className="grid gap-5">
                    <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
                        <div className="grid gap-2">
                            <Label htmlFor="firstName">First name</Label>
                            <Input
                                id="firstName"
                                placeholder="John"
                                autoComplete="given-name"
                                {...register("firstName")}
                            />
                            {errors.firstName && (
                                <p className="text-sm text-destructive">
                                    {errors.firstName.message}
                                </p>
                            )}
                        </div>

                        <div className="grid gap-2">
                            <Label htmlFor="lastName">Last name</Label>
                            <Input
                                id="lastName"
                                placeholder="Doe"
                                autoComplete="family-name"
                                {...register("lastName")}
                            />
                            {errors.lastName && (
                                <p className="text-sm text-destructive">
                                    {errors.lastName.message}
                                </p>
                            )}
                        </div>
                    </div>

                    <div className="grid gap-2">
                        <Label htmlFor="timeZone">Timezone</Label>
                        <Select
                            value={timeZone || ""}
                            onValueChange={(value) =>
                                setValue("timeZone", value, {
                                    shouldValidate: true,
                                    shouldDirty: true,
                                })
                            }
                        >
                            <SelectTrigger id="timeZone" className="w-full">
                                <SelectValue placeholder="Select a timezone" />
                            </SelectTrigger>
                            <SelectContent align="start">
                                <SelectGroup>
                                    {timeZones.map((tz) => (
                                        <SelectItem key={tz} value={tz}>
                                            {tz}
                                        </SelectItem>
                                    ))}
                                </SelectGroup>
                            </SelectContent>
                        </Select>
                        {errors.timeZone && (
                            <p className="text-sm text-destructive">
                                {errors.timeZone.message}
                            </p>
                        )}
                    </div>

                    <div className="flex justify-end">
                        <Button type="submit" disabled={isLoading}>
                            {isLoading && (
                                <Loader2Icon className="size-4 animate-spin" />
                            )}
                            Save
                        </Button>
                    </div>
                </form>
            </CardContent>
        </Card>
    );
};

export default ProfileTab;
