using SilentManager.API.Models.DB;
using SilentManagerApiServer.BusinessLogic.Dtos;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SilentManager.API.BusinessLogic.Validations
{
    public class EventValidations
    {
        public IEnumerable<ErrorResult> ValidateEventCreation(CompletedEvent _event)
        {
            IEnumerable<ErrorResult> errors = new List<ErrorResult>();
            if (_event.StartDate != null && _event.EndDate != null)
               errors.Union(ValidateDates(_event.StartDate, _event.EndDate));

            if (_event.Location == null) {
                // TODO Validar locations
            }

            errors.Union(ValidateLocation(_event.Location.Longitude,_event.Location.Latitude));

            return errors;
        }

        public IEnumerable<ErrorResult> ValidateDates(DateTime? start, DateTime? final)
        {
            List<ErrorResult> errors = new List<ErrorResult>();
            if (start > final) errors.Add(new ErrorResult(2, "Final date can not be lower than start date"));

            return errors;
        }

        public IEnumerable<ErrorResult> ValidateLocation(double? longitude, double? latitude)
        {
            List<ErrorResult> errors = new List<ErrorResult>();
            if (longitude == null && latitude == null) return errors;

            if (longitude > 180 || longitude<-180) errors.Add(new ErrorResult(2, "The valid range of latitude in degrees is -90 and +90 for the southern and northern hemisphere respectively"));
            if (latitude > 90 || latitude < -90) errors.Add(new ErrorResult(2, "Longitude is in the range -180 and +180 specifying coordinates west and east of the Prime Meridian"));
            return errors;
        }


    }
}