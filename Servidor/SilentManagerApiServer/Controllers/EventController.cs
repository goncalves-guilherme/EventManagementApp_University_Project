using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using SilentManagerApiServer.BusinessLogic.Dtos;
using SilentManagerApiServer.BusinessLogic.Service;
using SilentManager.API.Models.DB;
using SilentManager.API.Security;
using SilentManager.API.BusinessLogic.Validations;
using SilentManagerApiServer.Models.DB;
using SilentManager.API.BusinessLogic.Dtos;
using System.Globalization;

// For more information on enabling MVC for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace SilentManagerApiServer.Controllers
{
    [ApiVersion("1.0")]
    [Route("api/v{version:apiVersion}/events")]
    public class EventController : ControllerBase
    {
        private readonly IEventService _eventService;

        public EventController(IEventService eventService)
        {
            this._eventService = eventService;
        }

        [HttpGet]
        public async Task<JsonResult> GetEvents(
            [FromQuery(Name = "latitude")] string latitude,
            [FromQuery(Name = "longitude")] string longitude,
            [FromQuery(Name = "radius")] int? radius,
            [FromQuery(Name = "address")] string address,
            [FromQuery(Name = "page")] int? pageIndex,
            [FromQuery(Name = "date")] string date,
            [FromQuery(Name = "category")] string category)
        {

            if (!pageIndex.HasValue) pageIndex = 1;

            double lat = 0;
            double longi = 0;

            var format = new NumberFormatInfo();
            format.NegativeSign = "-";
            format.NumberDecimalSeparator = ".";
            if (!Double.TryParse(latitude, System.Globalization.NumberStyles.Any, format, out lat) ||
                !Double.TryParse(longitude, System.Globalization.NumberStyles.Any, format, out longi))
            {
                Response.StatusCode = 400;
                return new JsonResult(new ErrorResult(0, ""));
            }

            DateTime filterTime = new DateTime();
            DateTime? t = null;

            if (DateTime.TryParse(date, out filterTime)) {
                t = filterTime;
            }
            
            ResultWrapper<PagedResult<CompletedEvent>> events = await _eventService.GetEvents(lat, longi, radius.Value, pageIndex.Value, category, t);

            if (events.Errors != null)
            {
                Response.StatusCode = 400;
                return new JsonResult(events.Errors);
            }

            Response.StatusCode = 200;
            return new JsonResult(events.Result);
        }

        [HttpGet("{id}")]
        public async Task<JsonResult> GetByUserId(int id)
        {

            ResultWrapper<PagedResult<Event>> events = await _eventService.GetEventsByUser(id, 1);

            if (events.Errors != null)
            {
                Response.StatusCode = 401;
                return new JsonResult(events.Errors);
            }

            Response.StatusCode = 200;
            return new JsonResult(events.Result);
        }

        [HttpPut]
        public async Task<JsonResult> CreateEvent([FromBody]CreateEventDto newEvent)
        {
            DateTime startEventTime;
            DateTime endEventTime;

            if (newEvent == null || newEvent.Token == null || newEvent.Event == null || newEvent.Event.Location == null || !DateTime.TryParse(newEvent.Event.StartDate, out startEventTime)
                || !DateTime.TryParse(newEvent.Event.EndDate, out endEventTime))
            {
                Response.StatusCode = 400;
                return new JsonResult("Bad Request");
            }

            TokenDto token = TokenInDto.ConvertFromDTO(newEvent.Token);

            // EventDto to Model
            CompletedEvent completedEvent = new CompletedEvent
            {
                Description = newEvent.Event.Description,
                Name = newEvent.Event.Name,
                Location = newEvent.Event.Location,
                State = newEvent.Event.State,
                StartDate = startEventTime,
                EndDate = endEventTime,
                Radius = newEvent.Event.Radius,
                Category = newEvent.Event.Category,
                EventId = newEvent.Event.EventId != null ? newEvent.Event.EventId : 0
            };

            var events = await this._eventService.CreateEvent(completedEvent, token);

            if (events.Errors != null)
            {
                Response.StatusCode = 401;
                return new JsonResult(events.Errors);
            }

            Response.StatusCode = 200;
            return new JsonResult(events.Result);
        }

        [HttpDelete("{eventId}")]
        public async Task<JsonResult> DeleteEvent(int eventId, [FromHeader(Name = "Token")] string token)
        {
            TokenDto tk = new TokenDto
            {
                AccessToken = token
            };

            bool isDeleted = await this._eventService.DeleteEvent(eventId, tk);

            if (!isDeleted)
            {
                Response.StatusCode = 401;
                return new JsonResult(isDeleted);
            }

            Response.StatusCode = 200;
            return new JsonResult(isDeleted);
        }

        [HttpGet("users/me")]
        public async Task<JsonResult> GetUserEvents([FromHeader(Name = "Token")] string token, [FromQuery(Name = "page")] int? pageIndex)
        {
            if (token == null)
            {
                Response.StatusCode = 400;
                return new JsonResult("Bad Request");
            }

            TokenDto tk = new TokenDto
            {
                AccessToken = token
            };

            ResultWrapper<PagedResult<CompletedEvent>> events = await _eventService.GetUserEvents(tk, pageIndex);

            if (events.Errors != null)
            {
                Response.StatusCode = 401;
                return new JsonResult(events.Errors);
            }

            Response.StatusCode = 200;
            return new JsonResult(events.Result);
        }
    }
}
