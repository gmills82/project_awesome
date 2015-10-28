ALTER TABLE "public"."referral" ADD COLUMN "appt_kept" BOOLEAN DEFAULT NULL;

# --- !Downs
ALTER TABLE "public"."referral" DROP COLUMN "appt_kept";